package com.example.wellminder.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.wellminder.data.local.entities.FoodCategoryEntity
import com.example.wellminder.data.local.entities.FoodEntity
import com.example.wellminder.data.local.entities.FoodNutrientEntity
import com.example.wellminder.data.local.entities.FoodWithNutrientsAndCategory


@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrients(nutrients: FoodNutrientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: FoodCategoryEntity): Long

    @Transaction
    @Query("SELECT * FROM food")
    fun getAllFoodWithDetails(): kotlinx.coroutines.flow.Flow<List<FoodWithNutrientsAndCategory>>

    @Update
    suspend fun updateFood(food: FoodEntity)

    @Update
    suspend fun updateNutrients(nutrients: FoodNutrientEntity)

    @Delete
    suspend fun deleteFood(food: FoodEntity)

    @Transaction
    suspend fun updateFoodWithDetails(food: FoodEntity, nutrients: FoodNutrientEntity) {
        updateFood(food)
        updateNutrients(nutrients)
    }
}

