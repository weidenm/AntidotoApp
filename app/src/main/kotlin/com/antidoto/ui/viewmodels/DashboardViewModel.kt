package com.antidoto.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antidoto.data.db.entities.AppUsageEntry
import com.antidoto.data.db.entities.Goal
import com.antidoto.data.repository.CheckInRepository
import com.antidoto.data.repository.GoalRepository
import com.antidoto.data.repository.UsageStatsRepository
import com.antidoto.domain.model.DailyUsage
import com.antidoto.domain.model.DashboardData
import com.antidoto.domain.usecase.CalculateCostOfAttention
import com.antidoto.domain.usecase.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository,
    private val goalRepository: GoalRepository,
    private val checkInRepository: CheckInRepository,
    private val calculateCostOfAttention: CalculateCostOfAttention,
    private val streakCalculator: StreakCalculator,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _usageAccessGranted = MutableStateFlow(usageStatsRepository.hasUsageAccess())

    init {
        observeDashboard()
        refresh()
    }

    private fun observeDashboard() {
        val weekStart = GoalRepository.currentWeekStart()
        val weekEnd = weekStart.plusDays(DAYS_IN_WEEK - 1)

        combine(
            usageStatsRepository.getAppsForToday(),
            usageStatsRepository.getEntriesBetween(weekStart, weekEnd),
            goalRepository.getActiveGoals(),
            checkInRepository.observeCheckInDays(),
            _usageAccessGranted,
        ) { todayEntries, weekEntries, goals, checkInDays, accessGranted ->
            buildReadyState(todayEntries, weekEntries, goals, checkInDays, weekStart, accessGranted)
        }
            .onEach { _uiState.value = it }
            .catch { throwable ->
                _uiState.value = DashboardUiState.Error(throwable.message ?: "Erro ao carregar o painel")
            }
            .launchIn(viewModelScope)
    }

    private fun buildReadyState(
        todayEntries: List<AppUsageEntry>,
        weekEntries: List<AppUsageEntry>,
        goals: List<Goal>,
        checkInDays: List<LocalDate>,
        weekStart: LocalDate,
        accessGranted: Boolean,
    ): DashboardUiState.Ready {
        val sortedToday = todayEntries.sortedByDescending { it.durationMs }
        val totalTodayMs = sortedToday.sumOf { it.durationMs }

        val totalsByDate = weekEntries.groupBy { it.date }
            .mapValues { (_, entries) -> entries.sumOf { it.durationMs } }
        val weeklyUsage = (0 until DAYS_IN_WEEK).map { offset ->
            val date = weekStart.plusDays(offset)
            DailyUsage(date = date, totalMs = totalsByDate[date] ?: 0L)
        }

        val data = DashboardData(
            todayEntries = sortedToday,
            totalTodayMs = totalTodayMs,
            attentionCost = calculateCostOfAttention(totalTodayMs),
            weeklyUsage = weeklyUsage,
            goals = goals,
            currentStreakDays = streakCalculator.calculate(checkInDays),
        )
        return DashboardUiState.Ready(data = data, usageAccessGranted = accessGranted)
    }

    /** Re-check the usage-access grant and pull fresh stats from the device. */
    fun refresh() {
        viewModelScope.launch {
            _usageAccessGranted.value = usageStatsRepository.hasUsageAccess()
            runCatching { usageStatsRepository.syncWithDeviceStats() }
            _usageAccessGranted.value = usageStatsRepository.hasUsageAccess()
        }
    }

    companion object {
        private const val DAYS_IN_WEEK = 7L
    }
}
