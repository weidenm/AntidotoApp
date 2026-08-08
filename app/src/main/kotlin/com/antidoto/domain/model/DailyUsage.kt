package com.antidoto.domain.model

import java.time.LocalDate

/** Total foreground usage for a single day, used by the weekly trend chart. */
data class DailyUsage(
    val date: LocalDate,
    val totalMs: Long,
)
