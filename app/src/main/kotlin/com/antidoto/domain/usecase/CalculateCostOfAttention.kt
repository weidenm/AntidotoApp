package com.antidoto.domain.usecase

import com.antidoto.domain.model.AttentionCost
import javax.inject.Inject

/**
 * Projects a single day's usage into a yearly "attention cost" and translates it
 * into relatable units (books not read, courses not taken). The projection assumes
 * the current daily spend repeats every day of the year and is capped at the number
 * of hours in a year so extreme inputs stay sane.
 */
class CalculateCostOfAttention @Inject constructor() {

    operator fun invoke(dailyMillis: Long): AttentionCost {
        if (dailyMillis <= 0L) return AttentionCost(0, 0, 0)

        val dailyHours = dailyMillis.toDouble() / MILLIS_PER_HOUR
        val annualHours = (dailyHours * DAYS_PER_YEAR).coerceAtMost(MAX_HOURS_PER_YEAR)

        return AttentionCost(
            annualHours = annualHours.toInt(),
            booksNotRead = (annualHours / HOURS_PER_BOOK).toInt(),
            coursesNotDone = (annualHours / HOURS_PER_COURSE).toInt(),
        )
    }

    companion object {
        const val MILLIS_PER_HOUR = 3_600_000.0
        const val DAYS_PER_YEAR = 365
        const val MAX_HOURS_PER_YEAR = 8_760.0
        const val HOURS_PER_BOOK = 5.0
        const val HOURS_PER_COURSE = 40.0
    }
}
