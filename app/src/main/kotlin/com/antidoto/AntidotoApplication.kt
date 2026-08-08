package com.antidoto

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.antidoto.data.settings.SettingsRepository
import com.antidoto.notifications.NotificationHelper
import com.antidoto.notifications.ReminderScheduler
import com.antidoto.workers.SyncUsageWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class AntidotoApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleUsageSync()
        setUpReminders()
    }

    private fun setUpReminders() {
        notificationHelper.ensureChannel()
        if (settingsRepository.remindersEnabled) {
            reminderScheduler.schedule(settingsRepository.reminderHour)
        }
    }

    private fun scheduleUsageSync() {
        val request = PeriodicWorkRequestBuilder<SyncUsageWorker>(30, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncUsageWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
