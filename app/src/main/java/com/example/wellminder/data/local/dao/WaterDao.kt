package com.example.wellminder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wellminder.data.local.entities.WaterIntakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_intake WHERE date = :date AND userId = :userId LIMIT 1")
    fun getWaterIntake(date: String, userId: Long): Flow<WaterIntakeEntity?>

    @Query("SELECT * FROM water_intake WHERE date = :date AND userId = :userId LIMIT 1")
    suspend fun getWaterIntakeOneShot(date: String, userId: Long): WaterIntakeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: WaterIntakeEntity)

    @Query("DELETE FROM water_intake WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)
}
