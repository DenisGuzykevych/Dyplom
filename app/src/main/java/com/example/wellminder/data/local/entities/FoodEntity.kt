package com.example.wellminder.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "food",
    foreignKeys = [
        ForeignKey(
            entity = FoodCategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [androidx.room.Index("categoryId")]
)
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val foodId: Long = 0,
    val name: String,
    val categoryId: Long? = null // FK to FoodCategoryEntity, nullable if category not mandatory

)
