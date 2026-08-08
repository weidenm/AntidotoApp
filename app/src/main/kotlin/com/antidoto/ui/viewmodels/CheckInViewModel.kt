package com.antidoto.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antidoto.data.repository.CheckInRepository
import com.antidoto.domain.model.Mood
import com.antidoto.domain.model.Trigger
import com.antidoto.domain.usecase.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CheckInUiState(
    val streakDays: Int = 0,
    val todayCount: Int = 0,
)

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val checkInRepository: CheckInRepository,
    private val streakCalculator: StreakCalculator,
) : ViewModel() {

    private val startOfTodayMs =
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val uiState: StateFlow<CheckInUiState> = combine(
        checkInRepository.observeCheckInDays(),
        checkInRepository.getCheckInsSince(startOfTodayMs),
    ) { days, todayCheckIns ->
        CheckInUiState(
            streakDays = streakCalculator.calculate(days),
            todayCount = todayCheckIns.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CheckInUiState())

    fun recordCheckIn(mood: Mood, trigger: Trigger) {
        viewModelScope.launch {
            checkInRepository.recordCheckIn(mood, trigger)
        }
    }
}
