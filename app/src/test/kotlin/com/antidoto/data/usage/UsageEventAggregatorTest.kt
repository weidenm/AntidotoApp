package com.antidoto.data.usage

import com.antidoto.data.usage.UsageEventAggregator.AppEvent
import com.antidoto.data.usage.UsageEventAggregator.EventType.BACKGROUND
import com.antidoto.data.usage.UsageEventAggregator.EventType.FOREGROUND
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UsageEventAggregatorTest {

    private val windowStart = 0L
    private val windowEnd = 100_000L

    @Test
    fun `single complete session computes duration and one open`() {
        val events = listOf(
            AppEvent("com.instagram.android", FOREGROUND, 10_000L),
            AppEvent("com.instagram.android", BACKGROUND, 25_000L),
        )

        val result = UsageEventAggregator.aggregate(events, windowStart, windowEnd)

        assertEquals(1, result.size)
        assertEquals(15_000L, result[0].durationMs)
        assertEquals(1, result[0].openCount)
    }

    @Test
    fun `session open at window start counts duration from window start without open`() {
        val events = listOf(
            AppEvent("com.instagram.android", BACKGROUND, 5_000L),
        )

        val result = UsageEventAggregator.aggregate(events, windowStart, windowEnd)

        assertEquals(5_000L, result[0].durationMs)
        assertEquals(0, result[0].openCount)
    }

    @Test
    fun `session still open at window end counts duration until window end`() {
        val events = listOf(
            AppEvent("com.instagram.android", FOREGROUND, 90_000L),
        )

        val result = UsageEventAggregator.aggregate(events, windowStart, windowEnd)

        assertEquals(10_000L, result[0].durationMs)
        assertEquals(1, result[0].openCount)
    }

    @Test
    fun `separate sessions count separate opens`() {
        val events = listOf(
            AppEvent("com.instagram.android", FOREGROUND, 10_000L),
            AppEvent("com.instagram.android", BACKGROUND, 20_000L),
            AppEvent("com.instagram.android", FOREGROUND, 50_000L),
            AppEvent("com.instagram.android", BACKGROUND, 60_000L),
        )

        val result = UsageEventAggregator.aggregate(events, windowStart, windowEnd)

        assertEquals(20_000L, result[0].durationMs)
        assertEquals(2, result[0].openCount)
    }

    @Test
    fun `activity transitions inside merge gap do not inflate open count`() {
        val events = listOf(
            AppEvent("com.instagram.android", FOREGROUND, 10_000L),
            AppEvent("com.instagram.android", BACKGROUND, 20_000L),
            // Internal activity transition: resumed 500ms after pause
            AppEvent("com.instagram.android", FOREGROUND, 20_500L),
            AppEvent("com.instagram.android", BACKGROUND, 30_000L),
        )

        val result = UsageEventAggregator.aggregate(events, windowStart, windowEnd)

        assertEquals(1, result[0].openCount)
        assertEquals(19_500L, result[0].durationMs)
    }

    @Test
    fun `events outside window are ignored`() {
        val events = listOf(
            AppEvent("com.instagram.android", FOREGROUND, -5_000L),
            AppEvent("com.instagram.android", FOREGROUND, 10_000L),
            AppEvent("com.instagram.android", BACKGROUND, 20_000L),
            AppEvent("com.instagram.android", FOREGROUND, 200_000L),
        )

        val result = UsageEventAggregator.aggregate(events, windowStart, windowEnd)

        assertEquals(10_000L, result[0].durationMs)
        assertEquals(1, result[0].openCount)
    }

    @Test
    fun `multiple apps are aggregated independently`() {
        val events = listOf(
            AppEvent("com.instagram.android", FOREGROUND, 10_000L),
            AppEvent("com.tiktok.android", FOREGROUND, 15_000L),
            AppEvent("com.instagram.android", BACKGROUND, 20_000L),
            AppEvent("com.tiktok.android", BACKGROUND, 40_000L),
        )

        val result = UsageEventAggregator.aggregate(events, windowStart, windowEnd)
            .associateBy { it.packageName }

        assertEquals(10_000L, result.getValue("com.instagram.android").durationMs)
        assertEquals(25_000L, result.getValue("com.tiktok.android").durationMs)
    }

    @Test
    fun `duplicate foreground events count a single open`() {
        val events = listOf(
            AppEvent("com.instagram.android", FOREGROUND, 10_000L),
            AppEvent("com.instagram.android", FOREGROUND, 12_000L),
            AppEvent("com.instagram.android", BACKGROUND, 20_000L),
        )

        val result = UsageEventAggregator.aggregate(events, windowStart, windowEnd)

        assertEquals(1, result[0].openCount)
        assertEquals(10_000L, result[0].durationMs)
    }

    @Test
    fun `apps with zero duration are excluded`() {
        val events = listOf(
            AppEvent("com.instagram.android", FOREGROUND, 10_000L),
            AppEvent("com.instagram.android", BACKGROUND, 10_000L),
        )

        val result = UsageEventAggregator.aggregate(events, windowStart, windowEnd)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `empty event list produces empty result`() {
        val result = UsageEventAggregator.aggregate(emptyList(), windowStart, windowEnd)

        assertTrue(result.isEmpty())
    }
}
