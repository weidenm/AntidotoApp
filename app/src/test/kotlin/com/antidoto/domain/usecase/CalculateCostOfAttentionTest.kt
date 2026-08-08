package com.antidoto.domain.usecase

import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateCostOfAttentionTest {

    private val calculate = CalculateCostOfAttention()

    @Test
    fun `zero usage yields zero cost`() {
        val cost = calculate(0L)

        assertEquals(0, cost.annualHours)
        assertEquals(0, cost.booksNotRead)
        assertEquals(0, cost.coursesNotDone)
    }

    @Test
    fun `three hours a day projects to a year of hours, books and courses`() {
        val threeHours = TimeUnit.HOURS.toMillis(3)

        val cost = calculate(threeHours)

        // 3h * 365 = 1095h/year; 1095/5 = 219 books; 1095/40 = 27 courses
        assertEquals(1095, cost.annualHours)
        assertEquals(219, cost.booksNotRead)
        assertEquals(27, cost.coursesNotDone)
    }

    @Test
    fun `projection is capped at the number of hours in a year`() {
        val absurdDailyUsage = TimeUnit.HOURS.toMillis(30)

        val cost = calculate(absurdDailyUsage)

        assertEquals(8760, cost.annualHours)
    }

    @Test
    fun `negative input is treated as zero`() {
        val cost = calculate(-1_000L)

        assertEquals(0, cost.annualHours)
    }
}
