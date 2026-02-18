package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val email: String? = null, // Може бути null, якщо це Гість
    val passwordHash: String? = null, // Теж null для Гостя
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
