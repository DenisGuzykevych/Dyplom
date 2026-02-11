package com.example.wellminder.data.repository

import com.example.wellminder.data.local.dao.FoodDao
import com.example.wellminder.data.local.entities.FoodCategoryEntity
import com.example.wellminder.data.local.entities.FoodEntity
import com.example.wellminder.data.local.entities.FoodNutrientEntity
import com.example.wellminder.data.local.entities.FoodWithNutrientsAndCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodRepository @Inject constructor(
    private val foodDao: FoodDao,
    private val consumedFoodDao: com.example.wellminder.data.local.dao.ConsumedFoodDao,
    private val preferenceManager: com.example.wellminder.data.manager.PreferenceManager
) {
    fun getAllFood(): Flow<List<FoodWithNutrientsAndCategory>> {
        return foodDao.getAllFoodWithDetails()
    }

    fun getAllConsumedFood(): Flow<List<com.example.wellminder.data.local.dao.ConsumedFoodDetail>> {
        val userId = preferenceManager.userId
        if (userId == -1L) return kotlinx.coroutines.flow.flowOf(emptyList())
        return consumedFoodDao.getAllConsumedFood(userId)
    }

    fun getConsumedFoodForDate(date: java.time.LocalDate): Flow<List<com.example.wellminder.data.local.dao.ConsumedFoodDetail>> {
        val userId = preferenceManager.userId
        if (userId == -1L) return kotlinx.coroutines.flow.flowOf(emptyList())
        
        val startOfDay = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return consumedFoodDao.getConsumedFoodForDate(userId, startOfDay, endOfDay)
    }

    suspend fun saveFood(
        name: String,
        calories: Int,
        proteins: Float,
        fats: Float,
        carbs: Float,
        categoryName: String? = null
    ) {
        // 1. Create or Get Category (Simplified: just create for now, or null)
        val categoryId = if (categoryName != null) {
            foodDao.insertCategory(FoodCategoryEntity(name = categoryName))
        } else {
            null
        }

        // 2. Insert Food
        val foodId = foodDao.insertFood(
            FoodEntity(
                name = name,
                categoryId = categoryId
            )
        )

        // 3. Insert Nutrients
        foodDao.insertNutrients(
            FoodNutrientEntity(
                foodId = foodId,
                calories = calories,
                proteins = proteins,
                fats = fats,
                carbohydrates = carbs
            )
        )
    }

    suspend fun updateFood(
        foodId: Long,
        nutrientId: Long,
        name: String,
        calories: Int,
        proteins: Float,
        fats: Float,
        carbs: Float,
        categoryId: Long?
    ) {
        val food = FoodEntity(foodId = foodId, name = name, categoryId = categoryId)
        val nutrients = FoodNutrientEntity(
            nutrientId = nutrientId,
            foodId = foodId,
            calories = calories,
            proteins = proteins,
            fats = fats,
            carbohydrates = carbs
        )
        foodDao.updateFoodWithDetails(food, nutrients)
    }

    suspend fun deleteFood(foodId: Long) {
        val food = FoodEntity(foodId = foodId, name = "") 
        foodDao.deleteFood(food)
    }

    suspend fun logConsumedFood(foodId: Long, grams: Int, mealType: String) {
        val userId = preferenceManager.userId
        if (userId == -1L) return // Or throw error

        consumedFoodDao.insert(
            com.example.wellminder.data.local.entities.ConsumedFoodEntity(
                userId = userId,
                foodId = foodId,
                grams = grams,
                mealType = mealType
            )
        )
    }

    suspend fun updateConsumedFood(id: Long, foodId: Long, grams: Int, mealType: String, timestamp: Long) {
        val userId = preferenceManager.userId
        if (userId == -1L) return

        consumedFoodDao.update(
            com.example.wellminder.data.local.entities.ConsumedFoodEntity(
                id = id,
                userId = userId, // Ensure we don't accidentally switch user ownership
                foodId = foodId,
                grams = grams,
                mealType = mealType,
                timestamp = timestamp
            )
        )
    }

    suspend fun deleteConsumedFood(id: Long) {
        val userId = preferenceManager.userId
        // We can't easily validate ownership without fetching first, 
        // but let's assume UI only shows valid items.
        // We still need to pass a valid object structure for Room @Delete
        consumedFoodDao.delete(
            com.example.wellminder.data.local.entities.ConsumedFoodEntity(
                id = id,
                userId = userId, // Ideally irrelevant for delete by PK but good for completeness
                foodId = 0, 
                grams = 0,   
                mealType = "" 
            )
        )
    }
}
