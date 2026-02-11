package com.example.wellminder.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wellminder.data.local.dao.ConsumedFoodDao
import com.example.wellminder.data.local.dao.DailyStepsDao
import com.example.wellminder.data.local.dao.DailySummaryDao
import com.example.wellminder.data.local.dao.UserDao
import com.example.wellminder.data.local.dao.WaterDao
import com.example.wellminder.data.local.entities.DailySummaryEntity
import com.example.wellminder.data.manager.HealthConnectManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.ZoneId

@HiltWorker
class DailySummaryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userDao: UserDao,
    private val dailyStepsDao: DailyStepsDao,
    private val waterDao: WaterDao,
    private val consumedFoodDao: ConsumedFoodDao,
    private val dailySummaryDao: DailySummaryDao,
    private val healthConnectManager: HealthConnectManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val userIds = userDao.getAllUserIds()
            // Reset logic: Process data for "Yesterday" as the worker runs daily to finalize it.
            // The user views "Today" which starts empty (implicitly reset).
            // We save "Yesterday" to history.
            val yesterday = LocalDate.now().minusDays(1)
            val dateStr = yesterday.toString()

            val zoneId = ZoneId.systemDefault()
            val startOfDay = yesterday.atStartOfDay(zoneId).toInstant()
            val endOfDay = yesterday.plusDays(1).atStartOfDay(zoneId).toInstant()

            // Pre-fetch HC steps for yesterday (same for all users on device usually)
            var hcSteps = 0
            if (healthConnectManager.checkAvailability() == androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE &&
                healthConnectManager.hasAllPermissions()) {
                hcSteps = healthConnectManager.readSteps(startOfDay, endOfDay.minusMillis(1)).toInt()
            }

            userIds.forEach { userId ->
                // Check if already processed
                if (dailySummaryDao.getSummary(dateStr, userId) == null) {
                    
                    // 1. Manual Steps
                    val manualSteps = dailyStepsDao.getStepsOneShot(dateStr, userId)?.manualStepCount ?: 0
                    val totalSteps = hcSteps + manualSteps

                    // 2. Water
                    val water = waterDao.getWaterIntakeOneShot(dateStr, userId)?.intakeAmount ?: 0

                    // 3. Food (Calories & Macros)
                    val foodLogs = consumedFoodDao.getConsumedFoodForDateOneShot(
                        userId, 
                        startOfDay.toEpochMilli(), 
                        endOfDay.toEpochMilli() - 1
                    )
                    
                    var cals = 0
                    var prot = 0f
                    var fats = 0f
                    var carbs = 0f

                    foodLogs.forEach { detail ->
                        val ratio = detail.consumed.grams / 100f
                        cals += ((detail.nutrients?.calories ?: 0) * ratio).toInt()
                        prot += (detail.nutrients?.proteins ?: 0f) * ratio
                        fats += (detail.nutrients?.fats ?: 0f) * ratio
                        carbs += (detail.nutrients?.carbohydrates ?: 0f) * ratio
                    }

                    // 4. Weight (Snapshot: Last known weight on or before end of yesterday)
                    // We fetch logs from beginning of time up to end of yesterday
                    // Ideally we should limit or use a specific query, but getWeightLogs returns list.
                    // Let's use existing getWeightLogs(userId, 0) and filter in memory for now or assume efficient enough.
                    // Optimization: Add getLatestWeight(userId, beforeTimestamp) to DAO later.
                    // For now:
                    val weightLogs = userDao.getWeightLogs(userId, 0)
                    val lastWeightLog = weightLogs.filter { it.date < endOfDay.toEpochMilli() }.maxByOrNull { it.date }
                    val weight = lastWeightLog?.weightValue ?: userDao.getUserProfile(userId)?.currentWeight ?: 0f

                    // 5. Save Summary
                    val summary = DailySummaryEntity(
                        date = dateStr,
                        userId = userId,
                        totalSteps = totalSteps,
                        waterIntake = water,
                        caloriesConsumed = cals,
                        proteins = prot,
                        fats = fats,
                        carbs = carbs,
                        weight = weight
                    )
                    dailySummaryDao.insertSummary(summary)
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
