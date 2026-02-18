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
    val foodId: Long, // ID продукту, до якого це відноситься
    val proteins: Float, // білки (грами)
    val fats: Float, // жири (грами)
    val carbohydrates: Float, // вуглеводи (грами)
    val calories: Int // Калорії (вручну або пораховані)
)
