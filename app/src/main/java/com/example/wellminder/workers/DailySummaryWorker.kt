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
import com.example.wellminder.data.local.entities.DailyStepsEntity
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
            // Логіка скидання: Обробляємо дані за "Вчора", оскільки воркер запускається щодня для фіналізації.
            // Користувач бачить "Сьогодні", яке починається порожнім (скидання за замовчуванням).
            // Ми зберігаємо "Вчора" в історію.
            val yesterday = LocalDate.now().minusDays(1)
            val dateStr = yesterday.toString()

            val zoneId = ZoneId.systemDefault()
            val startOfDay = yesterday.atStartOfDay(zoneId).toInstant()
            val endOfDay = yesterday.plusDays(1).atStartOfDay(zoneId).toInstant()

            // Попередня вибірка кроків з Health Connect за вчора (зазвичай однаково для всіх користувачів на пристрої)
            var hcSteps = 0
            if (healthConnectManager.checkAvailability() == androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE &&
                healthConnectManager.hasAllPermissions()) {
                hcSteps = healthConnectManager.readSteps(startOfDay, endOfDay.minusMillis(1)).toInt()
            }

            userIds.forEach { userId ->
                // 1. Отримуємо кроки (з БД та Health Connect)
                var manualSteps = 0
                var sensorSteps = 0
                
                val dailySteps = dailyStepsDao.getStepsOneShot(dateStr, userId)
                manualSteps = dailySteps?.manualStepCount ?: 0
                sensorSteps = dailySteps?.sensorStepCount ?: 0 // Зберігаємо кроки крокоміра, якщо Health Connect не спрацює
                
                val profile = userDao.getUserProfile(userId)
                if (profile?.isHealthConnectEnabled == true && 
                    healthConnectManager.checkAvailability() == androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE &&
                    healthConnectManager.hasAllPermissions()) {
                    
                    val source = (applicationContext as? android.app.Application)?.getSharedPreferences("wellminder_prefs", Context.MODE_PRIVATE)
                        ?.getString("preferred_step_source", "all")

                    try {
                        val hcSteps = healthConnectManager.readStepsFiltered(startOfDay, endOfDay.minusMillis(1), source).toInt()
                        if (hcSteps > 0 || source != "pedometer") {
                            sensorSteps = hcSteps
                        }
                    } catch (e: Exception) {
                        // Якщо Android обмежує фоновий доступ до Health Connect, ігноруємо помилку, 
                        // щоб не втратити вже записані кроки в sensorSteps
                        e.printStackTrace()
                    }
                }

                // Зберігаємо кроки в БД для історії
                val updatedDailySteps = DailyStepsEntity(
                    date = dateStr,
                    userId = userId,
                    manualStepCount = manualSteps,
                    sensorStepCount = sensorSteps,
                    totalStepCount = manualSteps + sensorSteps
                )
                dailyStepsDao.insertOrUpdate(updatedDailySteps)

                // 2. Вага (Зріз: Остання відома вага)
                if (dailySummaryDao.getSummary(dateStr, userId) == null) {
                    val weightLogs = userDao.getWeightLogs(userId, 0)
                    val lastWeightLog = weightLogs.filter { it.date < endOfDay.toEpochMilli() }.maxByOrNull { it.date }
                    val weight = lastWeightLog?.weightValue ?: profile?.currentWeight ?: 0f

                    val summary = DailySummaryEntity(
                        date = dateStr,
                        userId = userId,
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


