package com.example.wellminder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wellminder.data.local.entities.DailySummaryEntity

@Dao
interface DailySummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: DailySummaryEntity)

    @Query("SELECT * FROM daily_summary WHERE date = :date AND userId = :userId")
    suspend fun getSummary(date: String, userId: Long): DailySummaryEntity?

    @Query("SELECT * FROM daily_summary WHERE userId = :userId ORDER BY date ASC")
    suspend fun getAllSummaries(userId: Long): List<DailySummaryEntity>
}
