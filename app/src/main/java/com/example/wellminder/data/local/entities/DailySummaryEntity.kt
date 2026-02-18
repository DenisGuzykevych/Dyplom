package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_summary", primaryKeys = ["date", "userId"])
data class DailySummaryEntity(
    val date: String, // "YYYY-MM-DD"
    val userId: Long,
    val weight: Float // Вага на кінець дня (знімок)
)
