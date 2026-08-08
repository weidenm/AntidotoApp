package com.antidoto.data.repository

import com.antidoto.data.db.entities.Lesson
import com.antidoto.testutil.FakeLessonDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LessonRepositoryTest {

    private fun lesson(id: String, order: Int) =
        Lesson(id = id, title = "t$order", content = "c$order", orderIndex = order)

    @Test
    fun `seed then complete updates counts and completedAt`() = runTest {
        val repo = LessonRepository(FakeLessonDao())
        repo.seedLessons(listOf(lesson("L0", 0), lesson("L1", 1)))

        assertEquals(0, repo.getCompletedCount().first())
        assertNull(repo.getLesson("L0").first()?.completedAt)

        repo.markCompleted("L0")

        assertEquals(1, repo.getCompletedCount().first())
        assertNotNull(repo.getLesson("L0").first()?.completedAt)
    }

    @Test
    fun `seeding is idempotent and keeps the first content`() = runTest {
        val repo = LessonRepository(FakeLessonDao())
        repo.seedLessons(listOf(lesson("L0", 0)))
        repo.seedLessons(listOf(Lesson(id = "L0", title = "changed", content = "changed", orderIndex = 0)))

        val all = repo.getAllLessons().first()
        assertEquals(1, all.size)
        assertEquals("t0", all.first().title)
    }
}
