package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "weight_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true)
    val logId: Long = 0,
    val userId: Long, // FK
    val date: Long, // Timestamp of entry
    val weightValue: Float
)
