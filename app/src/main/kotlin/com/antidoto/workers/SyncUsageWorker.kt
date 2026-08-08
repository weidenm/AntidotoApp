package com.antidoto.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.antidoto.data.repository.UsageStatsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncUsageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val usageStatsRepository: UsageStatsRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        usageStatsRepository.syncWithDeviceStats()
        Result.success()
    } catch (e: Exception) {
        if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
    }

    companion object {
        const val WORK_NAME = "sync_usage"
        private const val MAX_RETRIES = 3
    }
}
