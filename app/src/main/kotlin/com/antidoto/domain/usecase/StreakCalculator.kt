package com.antidoto.domain.usecase

import java.time.LocalDate
import javax.inject.Inject

/**
 * Computes the current check-in streak: the number of consecutive days, ending
 * today, that have at least one check-in. Yesterday counts as the anchor when
 * today has no check-in yet, so the streak stays alive until the day ends.
 */
class StreakCalculator @Inject constructor() {

    fun calculate(checkInDates: Collection<LocalDate>, today: LocalDate = LocalDate.now()): Int {
        if (checkInDates.isEmpty()) return 0
        val days = checkInDates.toHashSet()

        val anchor = when {
            days.contains(today) -> today
            days.contains(today.minusDays(1)) -> today.minusDays(1)
            else -> return 0
        }

        var streak = 0
        var cursor = anchor
        while (days.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
