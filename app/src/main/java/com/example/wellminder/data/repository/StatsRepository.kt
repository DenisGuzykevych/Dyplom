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
        return userDao.getUserProfile(userId)?.height ?: 175 // Default 175 cm
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

        // Strategy: "Two Variables"
        // We do NOT write to Health Connect. We only clear our old trash if it exists.
        // This ensures HC = Sensor Data Only.
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
