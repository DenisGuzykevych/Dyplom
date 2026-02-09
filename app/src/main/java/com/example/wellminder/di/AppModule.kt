package com.example.wellminder.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import com.example.wellminder.data.manager.HealthConnectManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHealthConnectClient(@ApplicationContext context: Context): HealthConnectClient {
        return HealthConnectClient.getOrCreate(context)
    }

    @Provides
    @Singleton
    fun provideHealthConnectManager(
        @ApplicationContext context: Context,
        healthConnectClient: HealthConnectClient
    ): HealthConnectManager {
        return HealthConnectManager(context, healthConnectClient)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): com.example.wellminder.data.local.AppDatabase {
        return androidx.room.Room.databaseBuilder(
            context,
            com.example.wellminder.data.local.AppDatabase::class.java,
            "wellminder_db"
        )
        .fallbackToDestructiveMigration() // For dev phase
        .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: com.example.wellminder.data.local.AppDatabase): com.example.wellminder.data.local.dao.UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideDailyStepsDao(database: com.example.wellminder.data.local.AppDatabase): com.example.wellminder.data.local.dao.DailyStepsDao {
        return database.dailyStepsDao()
    }

    @Provides
    @Singleton
    fun provideWaterDao(database: com.example.wellminder.data.local.AppDatabase): com.example.wellminder.data.local.dao.WaterDao {
        return database.waterDao()
    }

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): com.example.wellminder.data.manager.PreferenceManager {
        return com.example.wellminder.data.manager.PreferenceManager(context)
    }
}
