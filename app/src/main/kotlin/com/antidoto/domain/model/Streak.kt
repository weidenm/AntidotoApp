package com.antidoto.domain.model

import java.time.LocalDate

data class Streak(
    val checkInCount: Int,
    val lessonCount: Int,
    val lastDate: LocalDate?,
)
