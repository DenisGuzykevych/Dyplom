package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps", primaryKeys = ["date", "userId"])
data class DailyStepsEntity(
    val date: String, // Формат: "YYYY-MM-DD"
    val userId: Long,
    val manualStepCount: Int = 0
)
