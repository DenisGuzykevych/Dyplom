package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_summary", primaryKeys = ["date", "userId"])
data class DailySummaryEntity(
    val date: String, // YYYY-MM-DD
    val userId: Long,
    val totalSteps: Int,
    val waterIntake: Int,
    val caloriesConsumed: Int,
    val proteins: Float,
    val fats: Float,
    val carbs: Float,
    val weight: Float // Snapshot of weight at end of day
)
