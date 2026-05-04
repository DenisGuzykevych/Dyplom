package com.example.wellminder.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellminder.data.local.AppDatabase
import com.example.wellminder.data.manager.HealthConnectManager
import com.example.wellminder.data.manager.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val preferenceManager: PreferenceManager,
    private val appDatabase: AppDatabase,
    private val foodRepository: com.example.wellminder.data.repository.FoodRepository
) : ViewModel() {

    var steps by mutableIntStateOf(0)
        private set

    var permissionsGranted by mutableStateOf(false)
        private set

    var sdkStatus by mutableIntStateOf(0)
        private set
        
    var userProfile by mutableStateOf<com.example.wellminder.data.local.entities.UserProfileEntity?>(null)
        private set
        
    var userEmail by mutableStateOf<String?>(null)
        private set

    var userGoals by mutableStateOf<com.example.wellminder.data.local.entities.UserGoalEntity?>(null)
        private set

    var stepsBreakdown by mutableStateOf<Map<String, Long>>(emptyMap())
        private set

    var rawRecords by mutableStateOf<List<String>>(emptyList())
        private set
        
    var preferredSourceKey by mutableStateOf<String?>(null)
        private set

    var showHealthConnectInfo by mutableStateOf(false)
        private set

    private val _navigationEvent = Channel<String>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    val permissions = healthConnectManager.permissions
    
    init {
        viewModelScope.launch {
            preferenceManager.userIdFlow.collect { userId ->
               fetchUserProfile(userId)
            }
        }
    }
    
     // Визначаємо, як завантажувати профіль
    fun fetchUserProfile(userId: Long = preferenceManager.userId) {
        viewModelScope.launch {
            if (userId != -1L) {
                 appDatabase.userDao().getUserProfileFlow(userId).collect { profile ->
                     userProfile = profile
                     userGoals = appDatabase.userDao().getUserGoals(userId)
                     
                     if (profile != null) {
                         val user = appDatabase.userDao().getUserById(profile.userId)
                         userEmail = user?.email
                     }
                 }
            } else {
                userProfile = null
                userGoals = null
                userEmail = "Guest"
            }
        }
    }

    fun checkPermissions() {
        viewModelScope.launch {
            sdkStatus = healthConnectManager.checkAvailability() 
            if (sdkStatus == androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE) {
                permissionsGranted = healthConnectManager.hasAllPermissions()
                preferredSourceKey = preferenceManager.preferredStepSource ?: "all"
                
                // Показуємо інфо лише якщо дозволи ще не надані і ми ще не показували його
                if (!permissionsGranted && !preferenceManager.isHealthConnectInfoShown) {
                    showHealthConnectInfo = true
                }
                
                if (permissionsGranted) {
                    fetchSteps()
                }
            }
        }
    }

    fun onHealthConnectInfoDismissed() {
        showHealthConnectInfo = false
        preferenceManager.isHealthConnectInfoShown = true
    }

    fun selectStepSource(key: String) {
        viewModelScope.launch {
            preferenceManager.preferredStepSource = key
            preferredSourceKey = key
            fetchSteps()
        }
    }

    fun fetchSteps() {
        viewModelScope.launch {
            if (healthConnectManager.hasAllPermissions()) {
                val currentProfile = userProfile // Профіль вже завантажений у стейт ViewModel
                val syncStartTime = currentProfile?.healthConnectSyncStartTime?.let { Instant.ofEpochMilli(it) }

                val now = Instant.now()
                val startOfDay = now.atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                
                // Беремо пізніший час (початок дня або час синхронізації)
                val effectiveStartTime = if (syncStartTime != null && syncStartTime.isAfter(startOfDay)) {
                    syncStartTime
                } else {
                    startOfDay
                }
                
                // Якщо час старту пізніше ніж зараз -> поверне 0 (це ок)
                // Не запитуємо дані з майбутнього
                
                val source = preferenceManager.preferredStepSource
                steps = healthConnectManager.readStepsFiltered(effectiveStartTime, now, source).toInt()
                stepsBreakdown = healthConnectManager.getStepsBreakdown(effectiveStartTime, now)
                rawRecords = healthConnectManager.getRawStepRecords(effectiveStartTime, now)
            }
        }
    }
    
    fun toggleHealthConnect(enabled: Boolean) {
        viewModelScope.launch {
            val currentProfile = userProfile ?: return@launch
            
            // Якщо вмикаємо -> ставимо час "ПОЧАТОК СЬОГОДНІ", щоб не втратити ранкові кроки.
            val syncTime = if (enabled) {
                java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            } else null
            
            val updatedProfile = currentProfile.copy(
                isHealthConnectEnabled = enabled,
                healthConnectSyncStartTime = syncTime
            )
            appDatabase.userDao().updateProfile(updatedProfile)
            fetchUserProfile() // Оновлюємо UI
            if (enabled) {
                checkPermissions()
            }
        }
    }

    fun updateAccountData(
        newName: String, 
        newEmail: String, 
        newPassword: String? = null,
        onSuccess: () -> Unit = {}, 
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val currentProfile = userProfile ?: return@launch
            
            val currentUser = appDatabase.userDao().getUserById(currentProfile.userId) ?: return@launch
            
            // Якщо це гість, і він вводить email, він обов'язково має ввести пароль
            if (currentUser.isGuest && currentUser.email == null && newEmail.isNotBlank()) {
                if (newPassword.isNullOrBlank()) {
                    onError("Для прив'язки пошти до гостьового акаунту необхідно задати пароль")
                    return@launch
                }
            }
            
            // Оновлюємо ім'я
            val updatedProfile = currentProfile.copy(name = newName)
            appDatabase.userDao().updateProfile(updatedProfile)
            preferenceManager.userName = newName
            
            // Оновлюємо email та пароль
            try {
                var userToUpdate = currentUser
                var needsUpdate = false
                
                if (currentUser.email != newEmail) {
                    userToUpdate = userToUpdate.copy(email = newEmail)
                    needsUpdate = true
                }
                
                if (!newPassword.isNullOrBlank()) {
                    userToUpdate = userToUpdate.copy(passwordHash = newPassword)
                    needsUpdate = true
                }
                
                // Якщо був гостем і прив'язав пошту/пароль - більше не гість
                if (userToUpdate.isGuest && userToUpdate.email != null && userToUpdate.passwordHash != null) {
                    userToUpdate = userToUpdate.copy(isGuest = false)
                    needsUpdate = true
                }
                
                if (needsUpdate) {
                    appDatabase.userDao().updateUser(userToUpdate)
                    userEmail = newEmail
                }
                
                fetchUserProfile() // Оновлюємо UI
                onSuccess()
            } catch (e: android.database.sqlite.SQLiteConstraintException) {
                onError("Цей email вже використовується")
            } catch (e: Exception) {
                onError("Помилка при оновленні даних: ${e.message}")
            }
        }
    }

    fun saveGoalsAndStats(goal: String, weight: Float, height: Int, age: Int) {
        viewModelScope.launch {
            val currentProfile = userProfile ?: return@launch
            val currentGoals = userGoals ?: return@launch // Має існувати, якщо є профіль
            
            val updatedProfile = currentProfile.copy(
                currentWeight = weight,
                height = height,
                age = age
            )
            appDatabase.userDao().updateProfile(updatedProfile)
            
            // Зберігаємо зміну ваги в історію, щоб графік на вкладці Активність оновився
            appDatabase.userDao().insertWeightLog(
                com.example.wellminder.data.local.entities.WeightLogEntity(
                    userId = currentProfile.userId,
                    date = System.currentTimeMillis(),
                    weightValue = weight
                )
            )
            
            // Оновлюємо SharedPreferences
            preferenceManager.weight = weight
            preferenceManager.height = height.toFloat()
            preferenceManager.age = age

            // 2. Оновлюємо цілі
            val isMale = currentProfile.gender.equals("Male", ignoreCase = true) || 
                         currentProfile.gender.equals("Чоловік", ignoreCase = true) ||
                         currentProfile.gender.equals("Чоловіча", ignoreCase = true)

            val bmr = com.example.wellminder.util.GoalCalculator.calculateBMR(weight, height, age, isMale)
            val tdee = com.example.wellminder.util.GoalCalculator.calculateTDEE(bmr)
            
            val targetCalories = com.example.wellminder.util.GoalCalculator.calculateTargetCalories(tdee, goal)
            val (targetProteins, targetFats, targetCarbs) = com.example.wellminder.util.GoalCalculator.calculateMacros(targetCalories, goal)
            val targetWater = com.example.wellminder.util.GoalCalculator.calculateWaterTarget(weight)
            val targetSteps = com.example.wellminder.util.GoalCalculator.calculateStepTarget(goal)

            val updatedGoals = currentGoals.copy(
                goalType = goal,
                targetWeight = if (goal == "LOSE") weight - 5 else if (goal == "GAIN") weight + 5 else weight,
                targetProteins = targetProteins,
                targetFats = targetFats,
                targetCarbs = targetCarbs,
                targetWaterMl = targetWater,
                targetSteps = targetSteps
            )
            appDatabase.userDao().updateGoals(updatedGoals)
            
            fetchUserProfile() // Оновлюємо UI
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferenceManager.clear()
            _navigationEvent.send("login")
        }
    }
    
    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = preferenceManager.userId
            if (userId != -1L) {
               appDatabase.userDao().deleteUserById(userId) 
               appDatabase.waterDao().deleteByUserId(userId)
               appDatabase.dailyStepsDao().deleteByUserId(userId)
            }
            preferenceManager.clear()
            _navigationEvent.send("login")
        }
    }
}
