package com.antidoto.data.repository

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import com.antidoto.data.db.dao.AppUsageEntryDao
import com.antidoto.data.db.entities.AppUsageEntry
import com.antidoto.data.usage.UsageEventAggregator
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Singleton
class UsageStatsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appUsageEntryDao: AppUsageEntryDao,
) {

    fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getAppsForToday(): Flow<List<AppUsageEntry>> =
        appUsageEntryDao.getEntriesForDate(LocalDate.now())

    fun getEntriesBetween(start: LocalDate, end: LocalDate): Flow<List<AppUsageEntry>> =
        appUsageEntryDao.getEntriesBetween(start, end)

    fun getEntriesForApp(packageName: String, start: LocalDate, end: LocalDate): Flow<List<AppUsageEntry>> =
        appUsageEntryDao.getEntriesForApp(packageName, start, end)

    suspend fun syncWithDeviceStats() = withContext(Dispatchers.IO) {
        if (!hasUsageAccess()) return@withContext

        val today = LocalDate.now()
        val windowStartMs = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val windowEndMs = System.currentTimeMillis()

        val aggregated = UsageEventAggregator.aggregate(
            events = queryAppEvents(windowStartMs, windowEndMs),
            windowStartMs = windowStartMs,
            windowEndMs = windowEndMs,
        )

        val launchablePackages = launchablePackages()
        val entries = aggregated
            .filter { it.packageName in launchablePackages && it.packageName != context.packageName }
            .map { usage ->
                AppUsageEntry(
                    packageName = usage.packageName,
                    appName = resolveAppName(usage.packageName),
                    durationMs = usage.durationMs,
                    openCount = usage.openCount,
                    date = today,
                )
            }

        appUsageEntryDao.replaceForDate(today, entries)
    }

    private fun queryAppEvents(startMs: Long, endMs: Long): List<UsageEventAggregator.AppEvent> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageEvents = usageStatsManager.queryEvents(startMs, endMs)

        val result = mutableListOf<UsageEventAggregator.AppEvent>()
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            val type = when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> UsageEventAggregator.EventType.FOREGROUND
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED,
                -> UsageEventAggregator.EventType.BACKGROUND

                else -> null
            }
            if (type != null) {
                result += UsageEventAggregator.AppEvent(event.packageName, type, event.timeStamp)
            }
        }
        return result
    }

    private fun launchablePackages(): Set<String> {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return context.packageManager
            .queryIntentActivities(launcherIntent, 0)
            .map { it.activityInfo.packageName }
            .toSet()
    }

    private fun resolveAppName(packageName: String): String = try {
        val info = context.packageManager.getApplicationInfo(packageName, 0)
        context.packageManager.getApplicationLabel(info).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName
    }
}
