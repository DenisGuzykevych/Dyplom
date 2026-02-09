package com.example.wellminder.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profiles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val profileId: Long = 0,
    val userId: Long, // FK
    val name: String,
    val gender: String, // "Male", "Female", etc.
    val height: Int, // cm
    val currentWeight: Float, // kg
    val dateOfBirth: Long, // Timestamp or simplified date
    @ColumnInfo(name = "is_health_connect_enabled") val isHealthConnectEnabled: Boolean = false,
    @ColumnInfo(name = "health_connect_sync_start_time") val healthConnectSyncStartTime: Long? = null
)
