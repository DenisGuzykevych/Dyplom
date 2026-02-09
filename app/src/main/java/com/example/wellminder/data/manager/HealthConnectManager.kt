package com.example.wellminder.data.manager

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject

class HealthConnectManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val healthConnectClient: HealthConnectClient
) {
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class)
    )

    suspend fun checkAvailability(): Int {
        return HealthConnectClient.getSdkStatus(context)
    }

    suspend fun hasAllPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(permissions)
    }

    suspend fun readSteps(startTime: Instant, endTime: Instant): Long {
        return try {
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            val steps = response[StepsRecord.COUNT_TOTAL] ?: 0L
            android.util.Log.d("HealthConnectManager", "readSteps: $steps from $startTime to $endTime")
            steps
        } catch (e: Exception) {
            android.util.Log.e("HealthConnectManager", "readSteps error", e)
            e.printStackTrace()
            0L
        }
    }

    suspend fun getStepsBreakdown(startTime: Instant, endTime: Instant): Map<String, Long> {
        return try {
            val request = androidx.health.connect.client.request.ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = healthConnectClient.readRecords(request)
            
            val breakdown = mutableMapOf<String, Long>()
            response.records.forEach { record ->
                val packageName = record.metadata.dataOrigin.packageName
                val deviceModel = record.metadata.device?.model ?: "Unknown Device"
                val key = "$packageName ($deviceModel)"
                val count = record.count
                breakdown[key] = (breakdown[key] ?: 0L) + count
            }
            breakdown
        } catch (e: Exception) {
            android.util.Log.e("HealthConnectManager", "getStepsBreakdown error", e)
            emptyMap()
        }
    }

    suspend fun writeSteps(steps: Long, startTime: Instant, endTime: Instant) {
        try {
            android.util.Log.d("HealthConnectManager", "writeSteps: $steps from $startTime to $endTime")
            val record = StepsRecord(
                count = steps,
                startTime = startTime,
                endTime = endTime,
                startZoneOffset = java.time.ZoneOffset.systemDefault().rules.getOffset(startTime),
                endZoneOffset = java.time.ZoneOffset.systemDefault().rules.getOffset(endTime),
                metadata = Metadata(
                    recordingMethod = Metadata.RECORDING_METHOD_MANUAL_ENTRY
                )
            )
            healthConnectClient.insertRecords(listOf(record))
            android.util.Log.d("HealthConnectManager", "writeSteps: Success")
        } catch (e: Exception) {
            android.util.Log.e("HealthConnectManager", "writeSteps error", e)
            e.printStackTrace()
        }
    }

    /**
     * Deletes any steps written by WellMinder for the given date.
     * This ensures Health Connect only contains "Sensor" data from other apps.
     */
    suspend fun clearWellMinderSteps(date: java.time.LocalDate) {
        try {
            val startOfDay = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()
            val endOfDay = date.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().minusMillis(1)

            val request = androidx.health.connect.client.request.ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            val response = healthConnectClient.readRecords(request)

            val myPackageName = context.packageName
            val recordsToDelete = response.records.filter { 
                it.metadata.dataOrigin.packageName == myPackageName 
            }

            if (recordsToDelete.isNotEmpty()) {
                healthConnectClient.deleteRecords(
                    StepsRecord::class,
                    recordIdsList = recordsToDelete.map { it.metadata.id },
                    clientRecordIdsList = emptyList()
                )
                android.util.Log.d("HealthConnectManager", "Cleared ${recordsToDelete.size} WellMinder records to ensure pure sensor data.")
            }
        } catch (e: Exception) {
             android.util.Log.e("HealthConnectManager", "clearWellMinderSteps error", e)
        }
    }
    suspend fun getRawStepRecords(startTime: Instant, endTime: Instant): List<String> {
        return try {
            val request = androidx.health.connect.client.request.ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = healthConnectClient.readRecords(request)
            
            response.records.map { record ->
                val start = java.time.LocalDateTime.ofInstant(record.startTime, java.time.ZoneId.systemDefault()).toLocalTime()
                val end = java.time.LocalDateTime.ofInstant(record.endTime, java.time.ZoneId.systemDefault()).toLocalTime()
                val count = record.count
                val source = record.metadata.dataOrigin.packageName
                // Simplify source name
                val simpleSource = if (source.contains("xiaomi")) "Xiaomi" else if (source.contains("google")) "Google" else source
                "$start - $end: $count ($simpleSource)"
            }
        } catch (e: Exception) {
            android.util.Log.e("HealthConnectManager", "getRawStepRecords error", e)
            emptyList()
        }
    }
    
    suspend fun debugLogAllStepsForToday(date: java.time.LocalDate) {
        // ... kept for backward compatibility or can be removed if not used
    }
}
