package com.antidoto.data.usage

/**
 * Aggregates raw foreground/background transitions into per-app usage totals.
 *
 * Pure logic, isolated from UsageStatsManager so it can be unit-tested on the JVM.
 * Sessions separated by less than [DEFAULT_SESSION_MERGE_GAP_MS] are treated as one
 * "open" — activity transitions inside the same app emit pause/resume pairs that
 * should not inflate the open count.
 */
object UsageEventAggregator {

    const val DEFAULT_SESSION_MERGE_GAP_MS = 2_000L

    enum class EventType { FOREGROUND, BACKGROUND }

    data class AppEvent(
        val packageName: String,
        val type: EventType,
        val timestampMillis: Long,
    )

    data class AppUsage(
        val packageName: String,
        val durationMs: Long,
        val openCount: Int,
    )

    fun aggregate(
        events: List<AppEvent>,
        windowStartMs: Long,
        windowEndMs: Long,
        sessionMergeGapMs: Long = DEFAULT_SESSION_MERGE_GAP_MS,
    ): List<AppUsage> {
        val eventsByPackage = events
            .filter { it.timestampMillis in windowStartMs..windowEndMs }
            .sortedBy { it.timestampMillis }
            .groupBy { it.packageName }

        return eventsByPackage.map { (packageName, packageEvents) ->
            var durationMs = 0L
            var openCount = 0
            var foregroundSince = -1L
            var lastBackgroundAt = -1L

            for (event in packageEvents) {
                when (event.type) {
                    EventType.FOREGROUND -> {
                        if (foregroundSince < 0) {
                            foregroundSince = event.timestampMillis
                            val isContinuation = lastBackgroundAt >= 0 &&
                                event.timestampMillis - lastBackgroundAt <= sessionMergeGapMs
                            if (!isContinuation) openCount++
                        }
                    }

                    EventType.BACKGROUND -> {
                        // BACKGROUND without prior FOREGROUND: app was already open at window start
                        val since = if (foregroundSince >= 0) foregroundSince else windowStartMs
                        durationMs += event.timestampMillis - since
                        foregroundSince = -1L
                        lastBackgroundAt = event.timestampMillis
                    }
                }
            }

            if (foregroundSince >= 0) {
                durationMs += windowEndMs - foregroundSince
            }

            AppUsage(packageName, durationMs, openCount)
        }.filter { it.durationMs > 0 }
    }
}
