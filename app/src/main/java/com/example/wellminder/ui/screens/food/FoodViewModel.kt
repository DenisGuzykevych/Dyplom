package com.example.wellminder.ui.screens.food

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellminder.data.local.entities.FoodWithNutrients
import com.example.wellminder.data.local.entities.FoodEntity
import com.example.wellminder.data.local.entities.FoodNutrientEntity
import com.example.wellminder.data.repository.FoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

import com.example.wellminder.data.local.dao.UserDao
import com.example.wellminder.data.manager.PreferenceManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val repository: FoodRepository,
    private val userDao: UserDao,
    private val preferenceManager: PreferenceManager,
    private val appDatabase: com.example.wellminder.data.local.AppDatabase
) : ViewModel() {

    fun populateFoodDb(onComplete: (String) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val foodDao = appDatabase.foodDao()

                com.example.wellminder.data.local.InitialFoodData.list.forEach { item ->
                    // 1. Перевіряємо, чи такий продукт вже є
                    val existingFood = foodDao.getFoodByName(item.name)
                    if (existingFood == null) {
                        // Додаємо сам продукт
                        val food = FoodEntity(
                            name = item.name
                        )
                        val foodId = foodDao.insertFood(food)

                        // 2. Додаємо нутрієнти
                        val initialCalories = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(item.prot, item.fats, item.carbs)
                        val nutrients = FoodNutrientEntity(
                            foodId = foodId,
                            proteins = item.prot,
                            fats = item.fats,
                            carbohydrates = item.carbs,
                            calories = initialCalories
                        )
                        foodDao.insertNutrients(nutrients)
                    }
                }
                
                preferenceManager.isFoodPopulated = true
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete("Додано нові продукти!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete("Помилка: ${e.message}")
                }
            }
        }
    }

    var targetCalories by mutableIntStateOf(2000)
        private set

    init {
        viewModelScope.launch {
            preferenceManager.userIdFlow.collect { userId ->
                if (userId != -1L) {
                    val goals = userDao.getUserGoals(userId)
                    if (goals != null) {
                        targetCalories = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(
                            goals.targetProteins, goals.targetFats, goals.targetCarbs
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            val currentFood = repository.getAllFood().firstOrNull() ?: emptyList()
            if (!preferenceManager.isFoodPopulated || currentFood.isEmpty()) {
                populateFoodDb { /* тиха ініціалізація */ }
            }
        }
    }

    val foodList: StateFlow<List<FoodWithNutrients>> = repository.getAllFood()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = kotlinx.coroutines.flow.MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredFoodList: StateFlow<List<FoodWithNutrients>> = kotlinx.coroutines.flow.combine(
        foodList,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { it.food.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addFood(
        name: String,
        proteins: Float,
        fats: Float,
        carbs: Float,
        calories: Int
    ) {
        viewModelScope.launch {
            repository.saveFood(name, proteins, fats, carbs, calories)
        }
    }

    fun updateFood(
        foodWithDetails: FoodWithNutrients,
        name: String,
        proteins: Float,
        fats: Float,
        carbs: Float,
        calories: Int
    ) {
        val foodId = foodWithDetails.food.foodId
        val nutrientId = foodWithDetails.nutrients?.nutrientId ?: 0 // Обробити випадок, якщо нутрієнтів немає (хоча це дивно)

        viewModelScope.launch {
            repository.updateFood(
                foodId = foodId,
                nutrientId = nutrientId,
                name = name,
                proteins = proteins,
                fats = fats,
                carbs = carbs,
                calories = calories
            )
        }
    }

    private val _selectedDate = kotlinx.coroutines.flow.MutableStateFlow(java.time.LocalDate.now())
    val selectedDate: StateFlow<java.time.LocalDate> = _selectedDate.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val consumedFoodList: StateFlow<List<com.example.wellminder.data.local.dao.ConsumedFoodDetail>> = _selectedDate
        .flatMapLatest { date ->
            repository.getConsumedFoodForDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onDateChange(date: java.time.LocalDate) {
        _selectedDate.value = date
    }

    fun logConsumedFood(foodId: Long, grams: Int, mealType: String) {
        viewModelScope.launch {
            repository.logConsumedFood(foodId, grams, mealType)
        }
    }

    fun updateConsumedFood(id: Long, foodId: Long, grams: Int, mealType: String, timestamp: Long) {
        viewModelScope.launch {
            repository.updateConsumedFood(id, foodId, grams, mealType, timestamp)
        }
    }

    fun deleteConsumedFood(id: Long) {
        viewModelScope.launch {
            repository.deleteConsumedFood(id)
        }
    }

    fun deleteFood(foodWithDetails: FoodWithNutrients) {
        viewModelScope.launch {
            repository.deleteFood(foodWithDetails.food.foodId)
        }
    }
}
