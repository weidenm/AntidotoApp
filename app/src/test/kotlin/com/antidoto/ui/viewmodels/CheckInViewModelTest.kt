package com.antidoto.ui.viewmodels

import com.antidoto.data.repository.CheckInRepository
import com.antidoto.domain.model.Mood
import com.antidoto.domain.model.Trigger
import com.antidoto.domain.usecase.StreakCalculator
import com.antidoto.testutil.FakeCheckInDao
import com.antidoto.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckInViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `recording a check-in updates streak and today count`() = runTest {
        val repo = CheckInRepository(FakeCheckInDao())
        val viewModel = CheckInViewModel(repo, StreakCalculator())

        val collector = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()
        assertEquals(0, viewModel.uiState.value.streakDays)
        assertEquals(0, viewModel.uiState.value.todayCount)

        viewModel.recordCheckIn(Mood.WELL, Trigger.HABIT)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.streakDays)
        assertEquals(1, viewModel.uiState.value.todayCount)

        collector.cancel()
    }
}
