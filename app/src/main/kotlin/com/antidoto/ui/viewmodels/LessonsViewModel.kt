package com.antidoto.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antidoto.data.lessons.LessonSeed
import com.antidoto.data.repository.LessonRepository
import com.antidoto.domain.model.LessonStatus
import com.antidoto.domain.model.LessonTrailItem
import com.antidoto.domain.usecase.BuildLessonTrail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LessonsUiState(
    val items: List<LessonTrailItem> = emptyList(),
    val completedCount: Int = 0,
    val total: Int = 0,
)

@HiltViewModel
class LessonsViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val buildLessonTrail: BuildLessonTrail,
) : ViewModel() {

    init {
        // Idempotent: LessonDao.insertAll ignores conflicts on existing ids.
        viewModelScope.launch { lessonRepository.seedLessons(LessonSeed.lessons) }
    }

    val uiState: StateFlow<LessonsUiState> = lessonRepository.getAllLessons()
        .map { lessons ->
            val items = buildLessonTrail(lessons)
            LessonsUiState(
                items = items,
                completedCount = items.count { it.status == LessonStatus.COMPLETED },
                total = items.size,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LessonsUiState())
}
