package com.antidoto.domain.usecase

import com.antidoto.data.db.entities.Lesson
import com.antidoto.domain.model.LessonStatus
import com.antidoto.domain.model.LessonTrailItem
import javax.inject.Inject

/**
 * Turns the ordered lessons into trail items with an unlock state. Lessons unlock
 * sequentially: the first not-yet-completed lesson (with every earlier lesson
 * completed) is [LessonStatus.AVAILABLE]; later ones stay [LessonStatus.LOCKED]
 * until the trail reaches them.
 */
class BuildLessonTrail @Inject constructor() {

    operator fun invoke(lessons: List<Lesson>): List<LessonTrailItem> {
        val sorted = lessons.sortedBy { it.orderIndex }
        var allPreviousCompleted = true
        return sorted.map { lesson ->
            val completed = lesson.completedAt != null
            val status = when {
                completed -> LessonStatus.COMPLETED
                allPreviousCompleted -> LessonStatus.AVAILABLE
                else -> LessonStatus.LOCKED
            }
            if (!completed) allPreviousCompleted = false
            LessonTrailItem(lesson = lesson, status = status)
        }
    }
}
