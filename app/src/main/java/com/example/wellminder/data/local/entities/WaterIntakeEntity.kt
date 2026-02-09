package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_intake", primaryKeys = ["date", "userId"])
data class WaterIntakeEntity(
    val date: String, // Format: YYYY-MM-DD
    val userId: Long,
    val intakeAmount: Int = 0 // in milliliters
)
