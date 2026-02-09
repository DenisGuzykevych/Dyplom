package com.example.wellminder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wellminder.data.local.entities.DailyStepsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStepsDao {
    @Query("SELECT * FROM daily_steps WHERE date = :date AND userId = :userId")
    fun getSteps(date: String, userId: Long): Flow<DailyStepsEntity?>

    @Query("SELECT * FROM daily_steps WHERE date = :date AND userId = :userId")
    suspend fun getStepsOneShot(date: String, userId: Long): DailyStepsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(steps: DailyStepsEntity)
    
    @Query("DELETE FROM daily_steps WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)
}
