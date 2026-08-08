package com.antidoto.domain.model

import com.antidoto.data.db.entities.AppUsageEntry
import com.antidoto.data.db.entities.Goal

/** Everything the Home dashboard needs to render, assembled from the data layer. */
data class DashboardData(
    val todayEntries: List<AppUsageEntry>,
    val totalTodayMs: Long,
    val attentionCost: AttentionCost,
    val weeklyUsage: List<DailyUsage>,
    val goals: List<Goal>,
    val currentStreakDays: Int,
) {
    val hasUsageData: Boolean get() = totalTodayMs > 0 || weeklyUsage.any { it.totalMs > 0 }
}
