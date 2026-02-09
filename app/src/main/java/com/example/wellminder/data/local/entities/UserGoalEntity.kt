package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_goals",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val goalId: Long = 0,
    val userId: Long, // FK
    val targetWeight: Float,
    val targetWaterMl: Int,
    val targetSteps: Int,
    val targetCalories: Int,
    val goalType: String = "MAINTAIN" // "LOSE", "MAINTAIN", "GAIN"
)
