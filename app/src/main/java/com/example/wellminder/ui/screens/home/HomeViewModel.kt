package com.example.wellminder.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellminder.data.manager.HealthConnectManager
import com.example.wellminder.data.repository.StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val statsRepository: StatsRepository,
    private val preferenceManager: com.example.wellminder.data.manager.PreferenceManager,
    private val userDao: com.example.wellminder.data.local.dao.UserDao,
    private val foodRepository: com.example.wellminder.data.repository.FoodRepository
) : ViewModel() {

    var steps by mutableIntStateOf(0)
        private set

    var waterIntake by mutableIntStateOf(0)
        private set

    val permissions = healthConnectManager.permissions

    private val userId: Long
        get() = preferenceManager.userId

    var stepsPerKm by mutableIntStateOf(1300)
        private set

    var distanceKm by mutableFloatStateOf(0f)
        private set

    var consumedCalories by mutableIntStateOf(0)
        private set
    var consumedProteins by mutableFloatStateOf(0f)
        private set
    var consumedFats by mutableFloatStateOf(0f)
        private set
    var consumedCarbs by mutableFloatStateOf(0f)
        private set

    var targetCalories by mutableIntStateOf(2000) // За замовчуванням
        private set
    var targetProteins by mutableFloatStateOf(100f)
        private set
    var targetFats by mutableFloatStateOf(70f)
        private set
    var targetCarbs by mutableFloatStateOf(250f)
        private set

    init {
        viewModelScope.launch {
            preferenceManager.userIdFlow.collect { id ->
                fetchSteps()
                loadWaterAndStats(id)
                observeFoodLogs()
            }
        }
    }

    private fun observeFoodLogs() {
        viewModelScope.launch {
             foodRepository.getConsumedFoodForDate(LocalDate.now()).collect { logs ->
                 var cals = 0
                 var prot = 0f
                 var fats = 0f
                 var carbs = 0f
                 
                 logs.forEach { item ->
                     val ratio = item.consumed.grams / 100f
                     val p = (item.nutrients?.proteins ?: 0f) * ratio
                     val f = (item.nutrients?.fats ?: 0f) * ratio
                     val c = (item.nutrients?.carbohydrates ?: 0f) * ratio
                     
                     // Рахуємо калорії динамічно (по 3NF)
                     cals += com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(p, f, c)
                     prot += p
                     fats += f
                     carbs += c
                 }
                 
                 consumedCalories = cals
                 consumedProteins = prot
                 consumedFats = fats
                 consumedCarbs = carbs
             }
        }
    }


    
    // Відкриваємо ці поля для UI
    var stepTarget by mutableIntStateOf(10000)
        private set
    var waterTarget by mutableIntStateOf(2000)
        private set

    // Ця функція оновить ці значення
    private fun loadWaterAndStats(id: Long) {
        viewModelScope.launch {
            if (id != -1L) {
                statsRepository.getWaterIntake(LocalDate.now(), id).collect {
                    waterIntake = it
                }
            } else {
                waterIntake = 0
            }
        }
        viewModelScope.launch {
             if (id != -1L) {
                val height = statsRepository.getUserHeight(id)
                val stride = (height * 0.415) / 100.0
                stepsPerKm = (1000 / stride).toInt()
                
                // Підвантажуємо цілі (стежимо за змінами)
                userDao.getUserGoalsFlow(id).collect { goals ->
                    if (goals != null) {
                        targetProteins = goals.targetProteins
                        targetFats = goals.targetFats
                        targetCarbs = goals.targetCarbs
                        
                        // Рахуємо цільові калорії динамічно (3NF)
                        targetCalories = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(
                            targetProteins, targetFats, targetCarbs
                        )
                        
                        stepTarget = goals.targetSteps
                        waterTarget = goals.targetWaterMl
                    }
                }
            }
        }
    }


    fun fetchSteps() {
        viewModelScope.launch {
            if (userId == -1L) return@launch
            
            // Беремо кроки з Health Connect (сенсори) + Локальні (ручні)
            val manualSteps = statsRepository.getManualStepsOneShot(LocalDate.now(), userId)
            
            var sensorSteps = 0
            if (healthConnectManager.checkAvailability() == androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE) {
                if (healthConnectManager.hasAllPermissions()) {
                    val profile = userDao.getUserProfile(userId)
                    val syncStartTime = profile?.healthConnectSyncStartTime?.let { Instant.ofEpochMilli(it) }
                    
                    val now = Instant.now()
                    val today = now.atZone(ZoneId.systemDefault()).toLocalDate()
                    val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    
                    // Використовуємо пізніший час (startOfDay або syncStartTime), щоб не чіпляти чужі дані
                    val effectiveStartTime = if (syncStartTime != null && syncStartTime.isAfter(startOfDay)) {
                        syncStartTime
                    } else {
                        startOfDay
                    }
                    
                    val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1)

                    if (effectiveStartTime.isBefore(endOfDay)) {
                        val fetchedSteps = healthConnectManager.readSteps(effectiveStartTime, endOfDay).toInt()
                        sensorSteps = fetchedSteps
                    } else {
                         sensorSteps = 0
                    }
                    
                    android.util.Log.d("HomeViewModel", "fetchSteps: HC(Sensor)=$sensorSteps, Local(Manual)=$manualSteps")
                    
                    // DEBUG: Детальний лог
                    healthConnectManager.debugLogAllStepsForToday(today)
                }
            }
            
            steps = sensorSteps + manualSteps
            distanceKm = steps / stepsPerKm.toFloat()
            android.util.Log.d("HomeViewModel", "fetchSteps: Total Display = $steps, Dist=$distanceKm km")
        }
    }

    fun addSteps(amount: Int) {
        viewModelScope.launch {
             if (userId == -1L) return@launch
            android.util.Log.d("HomeViewModel", "addSteps: Requesting to add $amount steps")
            statsRepository.updateManualSteps(amount, userId)
            fetchSteps() // Refresh the total count from HC
        }
    }

    fun addWater(amount: Int) {
        viewModelScope.launch {
             if (userId == -1L) return@launch
            statsRepository.updateWaterIntake(amount, userId)
        }
    }

    suspend fun convertKmToSteps(km: Double): Int {
         if (userId == -1L) return 0
        val heightCm = statsRepository.getUserHeight(userId)
        // Середня довжина кроку: Зріст * 0.415
        val strideLengthMeters = (heightCm * 0.415) / 100.0
        val steps = (km * 1000 / strideLengthMeters).toInt()
        android.util.Log.d("HomeViewModel", "Converting $km km to steps. Height=$heightCm, Stride=$strideLengthMeters, Steps=$steps")
        return steps
    }
}
