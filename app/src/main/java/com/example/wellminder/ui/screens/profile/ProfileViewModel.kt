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
    private val appDatabase: AppDatabase
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
    
     // Determine fetchUserProfile signature change or overload
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
                if (permissionsGranted) {
                    fetchSteps()
                }
            }
        }
    }

    fun fetchSteps() {
        viewModelScope.launch {
            if (healthConnectManager.hasAllPermissions()) {
                val currentProfile = userProfile // userProfile is already loaded in VM state
                val syncStartTime = currentProfile?.healthConnectSyncStartTime?.let { Instant.ofEpochMilli(it) }

                val now = Instant.now()
                val startOfDay = now.atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                
                // Use the later of startOfDay or syncStartTime
                val effectiveStartTime = if (syncStartTime != null && syncStartTime.isAfter(startOfDay)) {
                    syncStartTime
                } else {
                    startOfDay
                }
                
                // If effective start time is after now (or very close), queries might return 0 which is correct.
                // We must ensure we don't query if effective > now (though readSteps handles it usually).
                
                steps = healthConnectManager.readSteps(effectiveStartTime, now).toInt()
                stepsBreakdown = healthConnectManager.getStepsBreakdown(effectiveStartTime, now)
                rawRecords = healthConnectManager.getRawStepRecords(effectiveStartTime, now)
            }
        }
    }
    
    fun toggleHealthConnect(enabled: Boolean) {
        viewModelScope.launch {
            val currentProfile = userProfile ?: return@launch
            
            // If enabling, set start time to NOW. If disabling, maybe clear it? 
            // Better to clear it on disable to reset the state.
            val syncTime = if (enabled) System.currentTimeMillis() else null
            
            val updatedProfile = currentProfile.copy(
                isHealthConnectEnabled = enabled,
                healthConnectSyncStartTime = syncTime
            )
            appDatabase.userDao().updateProfile(updatedProfile)
            fetchUserProfile() // Refresh UI
            if (enabled) {
                checkPermissions()
            }
        }
    }

    fun updateAccountData(newName: String) {
        viewModelScope.launch {
            val currentProfile = userProfile ?: return@launch
            val updatedProfile = currentProfile.copy(name = newName)
            appDatabase.userDao().updateProfile(updatedProfile)
            
            // Update Prefs
            preferenceManager.userName = newName
            
            fetchUserProfile() // Refresh
        }
    }

    fun saveGoalsAndStats(goal: String, weight: Float, height: Int, age: Int) {
        viewModelScope.launch {
            val currentProfile = userProfile ?: return@launch
            val currentGoals = userGoals ?: return@launch // Should exist if profile exists
            
            // 1. Update Profile (Body Stats)
            // Calculate birthDate from age (approx) if needed, or just keep existing dateOfBirth if age didn't change enough to warrant recalc?
            // Actually, we store dateOfBirth. Converting Age -> DOB is lossy. 
            // Better to only update DOB if we really want to. 
            // Let's assume for this "Modernization" we just update weight/height/goal. 
            // If the user expects Age to be saved, we must update DOB.
            // Let's approximate: DOB = Now - Age years.
            val estimatedDOB = java.time.LocalDate.now().minusYears(age.toLong())
                .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

            val updatedProfile = currentProfile.copy(
                currentWeight = weight,
                height = height,
                dateOfBirth = estimatedDOB
            )
            appDatabase.userDao().updateProfile(updatedProfile)
            
            // Update Prefs
            preferenceManager.weight = weight
            preferenceManager.height = height.toFloat()
            preferenceManager.age = age

            // 2. Update Goals
            val isMale = currentProfile.gender.equals("Male", ignoreCase = true) || 
                         currentProfile.gender.equals("Чоловік", ignoreCase = true) ||
                         currentProfile.gender.equals("Чоловіча", ignoreCase = true)

            val bmr = com.example.wellminder.util.GoalCalculator.calculateBMR(weight, height, age, isMale)
            val tdee = com.example.wellminder.util.GoalCalculator.calculateTDEE(bmr)
            
            val targetCalories = com.example.wellminder.util.GoalCalculator.calculateTargetCalories(tdee, goal)
            val targetWater = com.example.wellminder.util.GoalCalculator.calculateWaterTarget(weight)
            val targetSteps = com.example.wellminder.util.GoalCalculator.calculateStepTarget(goal)

            val updatedGoals = currentGoals.copy(
                goalType = goal,
                targetWeight = if (goal == "LOSE") weight - 5 else if (goal == "GAIN") weight + 5 else weight, 
                targetCalories = targetCalories,
                targetWaterMl = targetWater,
                targetSteps = targetSteps
            )
            appDatabase.userDao().updateGoals(updatedGoals)
            
            fetchUserProfile() // Refresh
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
