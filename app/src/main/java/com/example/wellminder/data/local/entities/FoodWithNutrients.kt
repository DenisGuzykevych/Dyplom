package com.example.wellminder.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class FoodWithNutrients(
    @Embedded val food: FoodEntity,
    
    @Relation(
        parentColumn = "foodId",
        entityColumn = "foodId"
    )
    val nutrients: FoodNutrientEntity?
)
