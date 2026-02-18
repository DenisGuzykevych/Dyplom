package com.example.wellminder.data.repository

import com.example.wellminder.data.local.dao.DailyStepsDao
import com.example.wellminder.data.local.entities.DailyStepsEntity
import com.example.wellminder.data.manager.HealthConnectManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class StatsRepository @Inject constructor(
    private val dailyStepsDao: DailyStepsDao,
    private val userDao: com.example.wellminder.data.local.dao.UserDao,
    private val waterDao: com.example.wellminder.data.local.dao.WaterDao,
    private val healthConnectManager: HealthConnectManager
) {
    suspend fun getUserHeight(userId: Long): Int {
        return userDao.getUserProfile(userId)?.height ?: 175 // За замовчуванням 175 см
    }

    fun getManualSteps(date: LocalDate, userId: Long): Flow<Int> {
        return dailyStepsDao.getSteps(date.toString(), userId).map { it?.manualStepCount ?: 0 }
    }

    suspend fun getManualStepsOneShot(date: LocalDate, userId: Long): Int {
        return dailyStepsDao.getStepsOneShot(date.toString(), userId)?.manualStepCount ?: 0
    }

    suspend fun updateManualSteps(amount: Int, userId: Long, date: LocalDate = LocalDate.now()) {
        val current = dailyStepsDao.getStepsOneShot(date.toString(), userId)?.manualStepCount ?: 0
        val newCount = (current + amount).coerceAtLeast(0)

        dailyStepsDao.insertOrUpdate(DailyStepsEntity(date.toString(), userId, newCount))

        // Стратегія: "Дві змінні"
        // Ми НЕ пишемо в Health Connect. Ми тільки чистимо старі дані, якщо вони там є.
        // Це гарантує, що в HC будуть тільки дані з сенсорів.
        if (healthConnectManager.hasAllPermissions()) {
            healthConnectManager.clearWellMinderSteps(date)
        }
    }
    
    fun getWaterIntake(date: LocalDate, userId: Long): Flow<Int> {
        return waterDao.getWaterIntake(date.toString(), userId).map { it?.intakeAmount ?: 0 }
    }

    suspend fun updateWaterIntake(amount: Int, userId: Long, date: LocalDate = LocalDate.now()) {
        val current = waterDao.getWaterIntakeOneShot(date.toString(), userId)?.intakeAmount ?: 0
        
        val newCount = (current + amount).coerceAtLeast(0)
        waterDao.insertOrUpdate(com.example.wellminder.data.local.entities.WaterIntakeEntity(date.toString(), userId, newCount))
    }
}
