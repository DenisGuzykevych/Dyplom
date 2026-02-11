package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_nutrients",
    foreignKeys = [
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["foodId"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("foodId")]

)
data class FoodNutrientEntity(
    @PrimaryKey(autoGenerate = true) val nutrientId: Long = 0,
    val foodId: Long, // FK linking to the food item
    val calories: Int, // kcal per 100g/serving
    val proteins: Float, // grams
    val fats: Float, // grams
    val carbohydrates: Float // grams
)
