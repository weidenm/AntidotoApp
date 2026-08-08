package com.antidoto.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antidoto.data.db.entities.AppUsageEntry
import com.antidoto.data.db.entities.Goal
import com.antidoto.data.repository.GoalRepository
import com.antidoto.data.repository.UsageStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GoalsUiState(
    val apps: List<AppUsageEntry> = emptyList(),
    val goalsByAppId: Map<String, Goal> = emptyMap(),
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val usageStatsRepository: UsageStatsRepository,
) : ViewModel() {

    val uiState: StateFlow<GoalsUiState> = combine(
        usageStatsRepository.getAppsForToday(),
        goalRepository.getActiveGoals(),
    ) { apps, goals ->
        GoalsUiState(
            apps = apps.sortedByDescending { it.durationMs },
            goalsByAppId = goals.associateBy { it.appId },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GoalsUiState())

    fun setGoal(appId: String, targetMinutes: Int) {
        viewModelScope.launch {
            goalRepository.saveGoal(
                Goal(
                    appId = appId,
                    weekStartDate = GoalRepository.currentWeekStart(),
                    targetMinutes = targetMinutes,
                    userSetMinutes = targetMinutes,
                ),
            )
        }
    }
}
