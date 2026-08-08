package com.antidoto.domain.model

import com.antidoto.data.db.entities.Lesson

/** A lesson paired with its unlock state for the trail UI. */
data class LessonTrailItem(
    val lesson: Lesson,
    val status: LessonStatus,
)
