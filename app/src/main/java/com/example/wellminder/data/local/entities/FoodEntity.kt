package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "food"
)
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val foodId: Long = 0,
    val name: String
)
