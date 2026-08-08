package com.antidoto.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.antidoto.workers.CheckInReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Schedules (or cancels) a daily check-in reminder at the user's chosen hour. */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val workManager = WorkManager.getInstance(context)

    fun schedule(hourOfDay: Int, now: LocalDateTime = LocalDateTime.now()) {
        val request = PeriodicWorkRequestBuilder<CheckInReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelayMinutes(hourOfDay, now), TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            CheckInReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun cancel() {
        workManager.cancelUniqueWork(CheckInReminderWorker.WORK_NAME)
    }

    private fun initialDelayMinutes(hourOfDay: Int, now: LocalDateTime): Long {
        var next = now.toLocalDate().atTime(LocalTime.of(hourOfDay.coerceIn(0, 23), 0))
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next).toMinutes().coerceAtLeast(1)
    }
}
