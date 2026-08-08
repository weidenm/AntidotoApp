package com.antidoto.data.repository

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalRepositoryTest {

    @Test
    fun `week start on monday returns same day`() {
        val monday = LocalDate.of(2026, 7, 6)

        assertEquals(monday, GoalRepository.currentWeekStart(monday))
    }

    @Test
    fun `week start mid week returns previous monday`() {
        val friday = LocalDate.of(2026, 7, 10)

        assertEquals(LocalDate.of(2026, 7, 6), GoalRepository.currentWeekStart(friday))
    }

    @Test
    fun `week start on sunday returns previous monday`() {
        val sunday = LocalDate.of(2026, 7, 12)

        assertEquals(LocalDate.of(2026, 7, 6), GoalRepository.currentWeekStart(sunday))
    }
}
