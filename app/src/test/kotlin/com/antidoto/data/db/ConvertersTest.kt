package com.antidoto.data.db

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `local date round trips through string`() {
        val date = LocalDate.of(2026, 7, 10)

        val stored = converters.fromLocalDate(date)
        val restored = converters.toLocalDate(stored)

        assertEquals(date, restored)
    }

    @Test
    fun `stored format is iso and lexicographically sortable`() {
        val earlier = converters.fromLocalDate(LocalDate.of(2026, 9, 30))!!
        val later = converters.fromLocalDate(LocalDate.of(2026, 10, 1))!!

        assertEquals("2026-09-30", earlier)
        assertEquals("2026-10-01", later)
        assertEquals(true, earlier < later)
    }

    @Test
    fun `null values are preserved`() {
        assertNull(converters.fromLocalDate(null))
        assertNull(converters.toLocalDate(null))
    }
}
