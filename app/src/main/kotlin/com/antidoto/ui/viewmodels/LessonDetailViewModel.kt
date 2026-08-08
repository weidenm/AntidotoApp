package com.antidoto.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antidoto.data.db.entities.Lesson
import com.antidoto.data.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LessonDetailUiState(
    val lesson: Lesson? = null,
)

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val lessonRepository: LessonRepository,
) : ViewModel() {

    private val lessonId: String = checkNotNull(savedStateHandle[ARG_LESSON_ID])

    val uiState: StateFlow<LessonDetailUiState> = lessonRepository.getLesson(lessonId)
        .map { LessonDetailUiState(lesson = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LessonDetailUiState())

    fun markCompleted() {
        viewModelScope.launch { lessonRepository.markCompleted(lessonId) }
    }

    companion object {
        const val ARG_LESSON_ID = "lessonId"
    }
}
