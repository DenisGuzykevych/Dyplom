package com.example.wellminder.ui.screens.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val userDao: com.example.wellminder.data.local.dao.UserDao,
    private val statsRepository: com.example.wellminder.data.repository.StatsRepository,
    private val foodRepository: com.example.wellminder.data.repository.FoodRepository,
    private val preferenceManager: com.example.wellminder.data.manager.PreferenceManager
) : ViewModel() {

    var currentWeight by mutableFloatStateOf(0f)
        private set

    var waterIntake by mutableIntStateOf(0)
        private set
    var waterTarget by mutableIntStateOf(2000)
        private set

    var stepCount by mutableIntStateOf(0)
        private set
    var stepTarget by mutableIntStateOf(10000) // Default
        private set

    var consumedCalories by mutableIntStateOf(0)
        private set
    var calorieTarget by mutableIntStateOf(2500) // Default
        private set

    var weeklyWeightData by mutableStateOf<List<Pair<String, Float>>>(emptyList())
        private set

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            preferenceManager.userIdFlow.collect { userId ->
                if (userId != -1L) {
                    launch { loadWeight(userId) }
                    launch { loadDailyStats(userId) }
                    launch { loadWeeklyWeight(userId) }
                }
            }
        }
    }

    private suspend fun loadWeeklyWeight(userId: Long) {
        val today = java.time.LocalDate.now()
        val sevenDaysAgo = today.minusDays(6)
        val startDate = sevenDaysAgo.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

        val logs = userDao.getWeightLogs(userId, startDate)
        
        // Map logs to date string (dd.MM) -> weight
        val logMap = logs.associate { log ->
            val date = java.time.Instant.ofEpochMilli(log.date).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM")
            date.format(formatter) to log.weightValue
        }

        val resultList = mutableListOf<Pair<String, Float>>()
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM")

        // Fill last 7 days
        for (i in 0..6) {
            val date = sevenDaysAgo.plusDays(i.toLong())
            val dateStr = date.format(formatter)
            // If no data, use 0 as requested
            val weight = logMap[dateStr] ?: 0f
            resultList.add(dateStr to weight)
        }
        weeklyWeightData = resultList
    }

    private suspend fun loadWeight(userId: Long) {
        val profile = userDao.getUserProfile(userId)
        currentWeight = profile?.currentWeight ?: 0f
    }

    private suspend fun loadDailyStats(userId: Long) = coroutineScope {
        val today = java.time.LocalDate.now()
        
        // Water
        launch {
            statsRepository.getWaterIntake(today, userId).collect {
                waterIntake = it
            }
        }
        
        // Steps
        launch {
            statsRepository.getManualSteps(today, userId).collect {
                stepCount = it
            }
        }

        // Calories
        launch {
             foodRepository.getConsumedFoodForDate(today).collect { logs ->
                 var cals = 0
                 logs.forEach { item ->
                     val ratio = item.consumed.grams / 100f
                     cals += ((item.nutrients?.calories ?: 0) * ratio).toInt()
                 }
                 consumedCalories = cals
             }
        }

        // Targets
        val goals = userDao.getUserGoals(userId)
        if (goals != null) {
            waterTarget = goals.targetWaterMl
            stepTarget = goals.targetSteps
            calorieTarget = goals.targetCalories
        }
    }

    private val _uiEvent = kotlinx.coroutines.flow.MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed interface UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent
    }

    fun saveWeight(newWeight: Float) {
        viewModelScope.launch {
            try {
                val userId = preferenceManager.userId
                if (userId != -1L) {
                    // Check if user actually exists in DB to prevent FK crash
                    val userExists = userDao.getUserById(userId) != null
                    if (!userExists) {
                        _uiEvent.emit(UiEvent.ShowSnackbar("Помилка: Користувача не знайдено в базі (ID: $userId)"))
                        return@launch
                    }

                    // 1. Log to weight history
                    userDao.insertWeightLog(
                        com.example.wellminder.data.local.entities.WeightLogEntity(
                            userId = userId,
                            date = System.currentTimeMillis(),
                            weightValue = newWeight
                        )
                    )
                    // 2. Update current profile weight
                    val profile = userDao.getUserProfile(userId)
                    if (profile != null) {
                        userDao.updateProfile(profile.copy(currentWeight = newWeight))
                        currentWeight = newWeight
                        launch { loadWeeklyWeight(userId) } // Refresh graph
                        _uiEvent.emit(UiEvent.ShowSnackbar("Вага збережена успішно"))
                    } else {
                        _uiEvent.emit(UiEvent.ShowSnackbar("Помилка: Профіль не знайдено"))
                    }
                } else {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Помилка: Користувач не авторизований"))
                }
            } catch (e: Exception) {
                android.util.Log.e("StatsViewModel", "Error saving weight", e)
                _uiEvent.emit(UiEvent.ShowSnackbar("Помилка збереження: ${e.message}"))
            }
        }
    }
}
