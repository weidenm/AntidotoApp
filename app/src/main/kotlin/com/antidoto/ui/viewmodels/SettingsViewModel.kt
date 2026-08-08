package com.antidoto.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.antidoto.data.settings.SettingsRepository
import com.antidoto.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val remindersEnabled: Boolean,
    val reminderHour: Int,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            remindersEnabled = settingsRepository.remindersEnabled,
            reminderHour = settingsRepository.reminderHour,
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setRemindersEnabled(enabled: Boolean) {
        settingsRepository.remindersEnabled = enabled
        _uiState.update { it.copy(remindersEnabled = enabled) }
        if (enabled) {
            reminderScheduler.schedule(settingsRepository.reminderHour)
        } else {
            reminderScheduler.cancel()
        }
    }

    fun setReminderHour(hour: Int) {
        settingsRepository.reminderHour = hour
        _uiState.update { it.copy(reminderHour = hour) }
        if (settingsRepository.remindersEnabled) {
            reminderScheduler.schedule(hour)
        }
    }
}
