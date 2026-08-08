package com.antidoto.ui.viewmodels

import com.antidoto.data.repository.LessonRepository
import com.antidoto.domain.model.LessonStatus
import com.antidoto.domain.usecase.BuildLessonTrail
import com.antidoto.testutil.FakeLessonDao
import com.antidoto.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LessonsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `viewmodel seeds the 15 lessons and marks the first available`() = runTest {
        val repo = LessonRepository(FakeLessonDao())
        val viewModel = LessonsViewModel(repo, BuildLessonTrail())

        val collector = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(15, state.total)
        assertEquals(0, state.completedCount)
        assertEquals(LessonStatus.AVAILABLE, state.items.first().status)
        assertEquals(LessonStatus.LOCKED, state.items[1].status)

        collector.cancel()
    }
}
