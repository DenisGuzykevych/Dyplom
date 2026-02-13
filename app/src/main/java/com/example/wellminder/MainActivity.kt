package com.example.wellminder // Trigger re-index

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.wellminder.ui.navigation.AppNavigation
import com.example.wellminder.ui.theme.WellMinderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @javax.inject.Inject
    lateinit var preferenceManager: com.example.wellminder.data.manager.PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startDest = if (preferenceManager.isLoggedIn) "home" else "login"
        
        scheduleDailySummaryWorker()
        schedulePopulateDatabaseWorker()

        setContent {
            WellMinderTheme {
                AppNavigation(startDestination = startDest)
            }
        }
    }

    private fun scheduleDailySummaryWorker() {
        val workManager = androidx.work.WorkManager.getInstance(applicationContext)
        val constraints = androidx.work.Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val currentDate = java.util.Calendar.getInstance()
        val dueDate = java.util.Calendar.getInstance()
        // Set Execution around 00:01:00.
        dueDate.set(java.util.Calendar.HOUR_OF_DAY, 0)
        dueDate.set(java.util.Calendar.MINUTE, 1)
        dueDate.set(java.util.Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(java.util.Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyWorkRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.wellminder.workers.DailySummaryWorker>(
             24, java.util.concurrent.TimeUnit.HOURS
        )
        .setConstraints(constraints)
        .setInitialDelay(timeDiff, java.util.concurrent.TimeUnit.MILLISECONDS)
        .addTag("DailySummary")
        .build()

        workManager.enqueueUniquePeriodicWork(
            "DailySummaryWorker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun schedulePopulateDatabaseWorker() {
        val workManager = androidx.work.WorkManager.getInstance(applicationContext)
        val request = androidx.work.OneTimeWorkRequest.Builder(com.example.wellminder.workers.PopulateDatabaseWorker::class.java)
            .build()
        
        workManager.enqueueUniqueWork(
            "PopulateDatabaseWorker_v3",
            androidx.work.ExistingWorkPolicy.KEEP,
            request
        )
    }
}