package com.example.wellminder.ui.screens.food

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellminder.data.local.entities.FoodWithNutrientsAndCategory
import com.example.wellminder.data.repository.FoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
                val categoriesMap = mutableMapOf<String, Long>()

                com.example.wellminder.data.local.InitialFoodData.list.forEach { item ->
                    // 1. Get or Insert Category
                    val categoryName = item.category
                    var categoryId = categoriesMap[categoryName]
                    
                    if (categoryId == null) {
                        val existingCategory = foodDao.getCategoryByName(categoryName)
                        if (existingCategory != null) {
                            categoryId = existingCategory.categoryId
                        } else {
                            val newCategory = com.example.wellminder.data.local.entities.FoodCategoryEntity(name = categoryName)
                            categoryId = foodDao.insertCategory(newCategory)
                        }
                        categoriesMap[categoryName] = categoryId!!
                    }

                    // 2. Check if Food exists
                    val existingFood = foodDao.getFoodByName(item.name)
                    if (existingFood == null) {
                        // Insert Food
                        val food = com.example.wellminder.data.local.entities.FoodEntity(
                            name = item.name,
                            categoryId = categoryId
                        )
                        val foodId = foodDao.insertFood(food)

                        // 3. Insert Nutrients
                        val nutrients = com.example.wellminder.data.local.entities.FoodNutrientEntity(
                            foodId = foodId,
                            calories = item.cals,
                            proteins = item.prot,
                            fats = item.fats,
                            carbohydrates = item.carbs
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
                        targetCalories = goals.targetCalories
                    }
                }
            }
        }
    }

    val foodList: StateFlow<List<FoodWithNutrientsAndCategory>> = repository.getAllFood()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = kotlinx.coroutines.flow.MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredFoodList: StateFlow<List<FoodWithNutrientsAndCategory>> = kotlinx.coroutines.flow.combine(
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
        calories: Int,
        proteins: Float,
        fats: Float,
        carbs: Float
    ) {
        viewModelScope.launch {
            repository.saveFood(name, calories, proteins, fats, carbs)
        }
    }

    fun updateFood(
        foodWithDetails: FoodWithNutrientsAndCategory,
        name: String,
        calories: Int,
        proteins: Float,
        fats: Float,
        carbs: Float
    ) {
        val foodId = foodWithDetails.food.foodId
        val nutrientId = foodWithDetails.nutrients?.nutrientId ?: 0 // Handle missing nutrients if any
        val categoryId = foodWithDetails.food.categoryId

        viewModelScope.launch {
            repository.updateFood(
                foodId = foodId,
                nutrientId = nutrientId,
                name = name,
                calories = calories,
                proteins = proteins,
                fats = fats,
                carbs = carbs,
                categoryId = categoryId
            )
        }
    }

    val consumedFoodList: StateFlow<List<com.example.wellminder.data.local.dao.ConsumedFoodDetail>> = repository.getAllConsumedFood()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    fun deleteFood(foodWithDetails: FoodWithNutrientsAndCategory) {
        viewModelScope.launch {
            repository.deleteFood(foodWithDetails.food.foodId)
        }
    }
}
