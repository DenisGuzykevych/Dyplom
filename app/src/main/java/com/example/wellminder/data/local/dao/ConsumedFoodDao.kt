package com.example.wellminder.data.local.dao

import androidx.room.*
import com.example.wellminder.data.local.entities.ConsumedFoodEntity
import com.example.wellminder.data.local.entities.FoodEntity
import com.example.wellminder.data.local.entities.FoodNutrientEntity
import kotlinx.coroutines.flow.Flow

data class ConsumedFoodDetail(
    @Embedded val consumed: ConsumedFoodEntity,
    @Relation(
        parentColumn = "foodId",
        entityColumn = "foodId"
    )
    val food: FoodEntity,
    
    @Relation(
        parentColumn = "foodId",
        entityColumn = "foodId"
    )
    val nutrients: FoodNutrientEntity?
)

@Dao
interface ConsumedFoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consumedFood: ConsumedFoodEntity)

    @Update
    suspend fun update(consumedFood: ConsumedFoodEntity)

    @Delete
    suspend fun delete(consumedFood: ConsumedFoodEntity)

    // Витягуємо їжу за конкретний діапазон дат
    @Transaction
    @Query("SELECT * FROM consumed_food WHERE userId = :userId AND timestamp BETWEEN :startOfDay AND :endOfDay ORDER BY timestamp DESC")
    fun getConsumedFoodForDate(userId: Long, startOfDay: Long, endOfDay: Long): Flow<List<ConsumedFoodDetail>>

    @Transaction
    @Query("SELECT * FROM consumed_food WHERE userId = :userId AND timestamp BETWEEN :startOfDay AND :endOfDay ORDER BY timestamp DESC")
    suspend fun getConsumedFoodForDateOneShot(userId: Long, startOfDay: Long, endOfDay: Long): List<ConsumedFoodDetail>

    // Взагалі вся історія, що юзер коли-небудь їв
    @Transaction
    @Query("SELECT * FROM consumed_food WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllConsumedFood(userId: Long): Flow<List<ConsumedFoodDetail>>
}
