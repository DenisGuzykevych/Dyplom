package com.example.wellminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.*
import com.example.wellminder.data.manager.PreferenceManager
import com.example.wellminder.ui.navigation.AppNavigation
import com.example.wellminder.ui.theme.WellMinderTheme
import com.example.wellminder.workers.DailySummaryWorker
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var userDao: com.example.wellminder.data.local.dao.UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        var startDest = "login"
        if (preferenceManager.isLoggedIn) {
            // Використовуємо корутину або runBlocking для простої перевірки при запуску
            startDest = "home"
        }
        
        // Ми робимо це всередині WellMinderTheme, щоб за потреби оновити навігацію,
        // або просто тут для початкового вибору екрана.
        
        scheduleDailySummaryWorker()

        setContent {
            WellMinderTheme {
                // Визначаємо справжній початковий екран реактивно
                val isLoggedIn = preferenceManager.isLoggedIn
                var finalStartDest by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(if (isLoggedIn) "home" else "login") }
                
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    if (isLoggedIn) {
                        val userId = preferenceManager.userId
                        val userExists = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            userDao.getUserById(userId) != null
                        }
                        if (!userExists) {
                            preferenceManager.clear()
                            finalStartDest = "login"
                        }
                    }
                }

                AppNavigation(startDestination = finalStartDest)
            }
        }
    }

    private fun scheduleDailySummaryWorker() {
        val workManager = WorkManager.getInstance(applicationContext)
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        // Встановлюємо виконання приблизно на 00:01:00.
        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(Calendar.MINUTE, 1)
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailySummaryWorker>(
             24, TimeUnit.HOURS
        )
        .setConstraints(constraints)
        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
        .addTag("DailySummary")
        .build()

        workManager.enqueueUniquePeriodicWork(
            "DailySummaryWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}