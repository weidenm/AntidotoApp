package com.antidoto.domain.usecase

import com.antidoto.data.db.entities.Lesson
import com.antidoto.domain.model.LessonStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class BuildLessonTrailTest {

    private val build = BuildLessonTrail()

    private fun lesson(order: Int, completed: Boolean) = Lesson(
        id = "L$order",
        title = "Lesson $order",
        content = "...",
        completedAt = if (completed) 1_000L else null,
        orderIndex = order,
    )

    @Test
    fun `first lesson available, rest locked when nothing completed`() {
        val trail = build(List(3) { lesson(it, completed = false) })

        assertEquals(LessonStatus.AVAILABLE, trail[0].status)
        assertEquals(LessonStatus.LOCKED, trail[1].status)
        assertEquals(LessonStatus.LOCKED, trail[2].status)
    }

    @Test
    fun `completing a lesson unlocks the next`() {
        val trail = build(
            listOf(
                lesson(0, completed = true),
                lesson(1, completed = false),
                lesson(2, completed = false),
            ),
        )

        assertEquals(LessonStatus.COMPLETED, trail[0].status)
        assertEquals(LessonStatus.AVAILABLE, trail[1].status)
        assertEquals(LessonStatus.LOCKED, trail[2].status)
    }

    @Test
    fun `all completed shows all completed`() {
        val trail = build(List(3) { lesson(it, completed = true) })

        assertEquals(listOf(LessonStatus.COMPLETED, LessonStatus.COMPLETED, LessonStatus.COMPLETED), trail.map { it.status })
    }

    @Test
    fun `input is sorted by order index`() {
        val trail = build(
            listOf(
                lesson(2, completed = false),
                lesson(0, completed = true),
                lesson(1, completed = false),
            ),
        )

        assertEquals(listOf("L0", "L1", "L2"), trail.map { it.lesson.id })
        assertEquals(LessonStatus.AVAILABLE, trail[1].status)
    }

    @Test
    fun `empty input yields empty trail`() {
        assertEquals(emptyList<Any>(), build(emptyList()))
    }
}
