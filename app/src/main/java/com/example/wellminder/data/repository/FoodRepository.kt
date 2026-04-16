package com.example.wellminder.data.repository

import com.example.wellminder.data.local.dao.FoodDao
import com.example.wellminder.data.local.entities.FoodEntity
import com.example.wellminder.data.local.entities.FoodNutrientEntity
import com.example.wellminder.data.local.entities.FoodWithNutrients
import com.example.wellminder.data.remote.NutrimentsData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodRepository @Inject constructor(
    private val foodDao: FoodDao,
    private val consumedFoodDao: com.example.wellminder.data.local.dao.ConsumedFoodDao,
    private val preferenceManager: com.example.wellminder.data.manager.PreferenceManager,
    private val openFoodFactsApi: com.example.wellminder.data.remote.OpenFoodFactsApi
) {
    fun getAllFood(): Flow<List<FoodWithNutrients>> {
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
        proteins: Float,
        fats: Float,
        carbs: Float,
        calories: Int,
        barcode: String? = null
    ) {
        // 1. Спочатку створюємо сам продукт
        val foodId = foodDao.insertFood(
            FoodEntity(
                name = name,
                barcode = barcode
            )
        )

        // 2. Тепер додаємо його БЖВ
        foodDao.insertNutrients(
            FoodNutrientEntity(
                foodId = foodId,
                proteins = proteins,
                fats = fats,
                carbohydrates = carbs,
                calories = calories
            )
        )
    }

    suspend fun updateFood(
        foodId: Long,
        nutrientId: Long,
        name: String,
        proteins: Float,
        fats: Float,
        carbs: Float,
        calories: Int,
        barcode: String? = null
    ) {
        val food = FoodEntity(foodId = foodId, name = name, barcode = barcode)
        val nutrients = FoodNutrientEntity(
            nutrientId = nutrientId,
            foodId = foodId,
            proteins = proteins,
            fats = fats,
            carbohydrates = carbs,
            calories = calories
        )
        foodDao.updateFoodWithDetails(food, nutrients)
    }

    suspend fun deleteFood(foodId: Long) {
        val food = FoodEntity(foodId = foodId, name = "") 
        foodDao.deleteFood(food)
    }

    suspend fun logConsumedFood(foodId: Long, grams: Int, mealType: String) {
        val userId = preferenceManager.userId
        if (userId == -1L) return // Або можна кинути помилку

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
                userId = userId, // Переконуємось, що не міняємо власника запису випадково
                foodId = foodId,
                grams = grams,
                mealType = mealType,
                timestamp = timestamp
            )
        )
    }

    suspend fun deleteConsumedFood(id: Long) {
        val userId = preferenceManager.userId
        // Важко перевірити власника без зайвого запиту, 
        // але вважаємо, що UI показує тільки дозволене.
        // Для Room @Delete треба передати об'єкт з правильним PK
        consumedFoodDao.delete(
            com.example.wellminder.data.local.entities.ConsumedFoodEntity(
                id = id,
                userId = userId, // Це поле не важливе для видалення по ID, але хай буде для порядку
                foodId = 0, 
                grams = 0,   
                mealType = "" 
            )
        )
    }

    suspend fun findProductByBarcode(barcode: String): FoodWithNutrients? {
        // 1. Спочатку шукаємо в локальній БД
        val localProduct = foodDao.getFoodByBarcode(barcode)
        if (localProduct != null) return localProduct

        // 2. Якщо немає локально, шукаємо в API
        return try {
            val response = openFoodFactsApi.getProduct(barcode)
            android.util.Log.d("FoodRepository", "API Response for $barcode: status=${response.status}, product=${response.product?.productName}")
            
            if (response.status == 1 && response.product != null) {
                val product = response.product
                val n = product.nutriments
                
                if (n != null) {
                    android.util.Log.d("FoodRepository", "Nutriments: prot=${n.proteins100g}, fat=${n.fat100g}, carbs=${n.carbohydrates100g}, kcal=${n.energyKcal100g}")
                } else {
                    android.util.Log.w("FoodRepository", "Nutriments object is NULL for $barcode")
                }

                val proteins = toSafeFloat(n?.proteins100g)
                val fats = toSafeFloat(n?.fat100g)
                val carbs = toSafeFloat(n?.carbohydrates100g)
                val kcal = toSafeFloat(n?.energyKcal100g ?: n?.energyKcal)
                
                // Створюємо об'єкт для UI
                FoodWithNutrients(
                    food = FoodEntity(
                        name = product.productName ?: "Невідомий продукт",
                        barcode = barcode
                    ),
                    nutrients = FoodNutrientEntity(
                        foodId = 0,
                        proteins = proteins,
                        fats = fats,
                        carbohydrates = carbs,
                        calories = kcal.toInt()
                    )
                )
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("FoodRepository", "Error fetching product by barcode", e)
            null
        }
    }

    private fun toSafeFloat(value: Any?): Float {
        return when (value) {
            is Number -> value.toFloat()
            is String -> value.toFloatOrNull() ?: 0f
            else -> 0f
        }
    }
}
