package com.example.wellminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wellminder.data.local.dao.FoodDao
import com.example.wellminder.data.local.dao.UserDao
import com.example.wellminder.data.local.entities.FoodEntity
import com.example.wellminder.data.local.entities.UserEntity
import com.example.wellminder.data.local.entities.UserGoalEntity
import com.example.wellminder.data.local.entities.UserProfileEntity
import com.example.wellminder.data.local.entities.WeightLogEntity

@Database(
    entities = [
        FoodEntity::class, 
        UserEntity::class, 
        UserProfileEntity::class, 
        UserGoalEntity::class, 
        WeightLogEntity::class,
        com.example.wellminder.data.local.entities.DailyStepsEntity::class,
        com.example.wellminder.data.local.entities.WaterIntakeEntity::class,
        com.example.wellminder.data.local.entities.FoodCategoryEntity::class,
        com.example.wellminder.data.local.entities.FoodNutrientEntity::class,
        com.example.wellminder.data.local.entities.ConsumedFoodEntity::class,
        com.example.wellminder.data.local.entities.DailySummaryEntity::class
    ], 
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun userDao(): UserDao
    abstract fun dailyStepsDao(): com.example.wellminder.data.local.dao.DailyStepsDao
    abstract fun waterDao(): com.example.wellminder.data.local.dao.WaterDao
    abstract fun consumedFoodDao(): com.example.wellminder.data.local.dao.ConsumedFoodDao
    abstract fun dailySummaryDao(): com.example.wellminder.data.local.dao.DailySummaryDao
}
