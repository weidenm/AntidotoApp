package com.antidoto.ui.viewmodels

import com.antidoto.domain.model.DashboardData

sealed interface DashboardUiState {
    data object Loading : DashboardUiState

    data class Ready(
        val data: DashboardData,
        val usageAccessGranted: Boolean,
    ) : DashboardUiState

    data class Error(val message: String) : DashboardUiState
}
