package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "consumed_food",
    foreignKeys = [
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["foodId"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("foodId"), Index("userId")]
)
data class ConsumedFoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long, // Link to User
    val foodId: Long,
    val grams: Int,
    val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack"
    val timestamp: Long = System.currentTimeMillis()
)
