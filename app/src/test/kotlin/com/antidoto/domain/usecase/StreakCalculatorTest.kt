package com.antidoto.domain.usecase

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {

    private val calculator = StreakCalculator()
    private val today = LocalDate.of(2026, 8, 8)

    @Test
    fun `no check-ins is a zero streak`() {
        assertEquals(0, calculator.calculate(emptyList(), today))
    }

    @Test
    fun `check-in today only is a streak of one`() {
        assertEquals(1, calculator.calculate(listOf(today), today))
    }

    @Test
    fun `consecutive days ending today count fully`() {
        val dates = listOf(today, today.minusDays(1), today.minusDays(2))
        assertEquals(3, calculator.calculate(dates, today))
    }

    @Test
    fun `yesterday keeps the streak alive when today has no check-in yet`() {
        val dates = listOf(today.minusDays(1), today.minusDays(2))
        assertEquals(2, calculator.calculate(dates, today))
    }

    @Test
    fun `a gap breaks the streak`() {
        val dates = listOf(today, today.minusDays(1), today.minusDays(3), today.minusDays(4))
        assertEquals(2, calculator.calculate(dates, today))
    }

    @Test
    fun `only old check-ins yield zero`() {
        val dates = listOf(today.minusDays(5), today.minusDays(6))
        assertEquals(0, calculator.calculate(dates, today))
    }

    @Test
    fun `duplicate dates are counted once`() {
        val dates = listOf(today, today, today.minusDays(1))
        assertEquals(2, calculator.calculate(dates, today))
    }
}
