package com.example.wellminder.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wellminder.data.local.InitialFoodData
import com.example.wellminder.data.local.dao.FoodDao
import com.example.wellminder.data.local.entities.FoodCategoryEntity
import com.example.wellminder.data.local.entities.FoodEntity
import com.example.wellminder.data.local.entities.FoodNutrientEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PopulateDatabaseWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val foodDao: FoodDao,
    private val preferenceManager: com.example.wellminder.data.manager.PreferenceManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // For v3, we force run to ensure data is correct, then set flag.
            // if (preferenceManager.isFoodPopulated) {
            //    return@withContext Result.success()
            // }

            // Optional: Double check count if you want to avoid duplicates if flag was somehow missed but DB full
            // But relying on flag is safer for the "I added 1 item manually" case.
            
            val categoriesMap = mutableMapOf<String, Long>()

            InitialFoodData.list.forEach { item ->
                // 1. Get or Insert Category
                val categoryName = item.category
                var categoryId = categoriesMap[categoryName]
                
                if (categoryId == null) {
                    val existingCategory = foodDao.getCategoryByName(categoryName)
                    if (existingCategory != null) {
                        categoryId = existingCategory.categoryId
                    } else {
                        val newCategory = FoodCategoryEntity(name = categoryName)
                        categoryId = foodDao.insertCategory(newCategory)
                    }
                    categoriesMap[categoryName] = categoryId!!
                }

                // 2. Check if Food exists
                val existingFood = foodDao.getFoodByName(item.name)
                if (existingFood == null) {
                    // Insert Food
                    val food = FoodEntity(
                        name = item.name,
                        categoryId = categoryId
                    )
                    val foodId = foodDao.insertFood(food)

                    // 3. Insert Nutrients
                    val nutrients = FoodNutrientEntity(
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
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
