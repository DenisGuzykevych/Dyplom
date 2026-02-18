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
    val userId: Long, // ID юзера, який це з'їв
    val foodId: Long,
    val grams: Int,
    val mealType: String, // "Breakfast" (Сніданок), "Lunch" (Обід), "Dinner" (Вечеря), "Snack" (Перекус)
    val timestamp: Long = System.currentTimeMillis()
)
