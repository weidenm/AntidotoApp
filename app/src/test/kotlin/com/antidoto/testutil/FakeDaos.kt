package com.antidoto.testutil

import com.antidoto.data.db.dao.CheckInDao
import com.antidoto.data.db.dao.GoalDao
import com.antidoto.data.db.dao.LessonDao
import com.antidoto.data.db.entities.CheckIn
import com.antidoto.data.db.entities.Goal
import com.antidoto.data.db.entities.Lesson
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory CheckInDao for JVM unit tests. */
class FakeCheckInDao : CheckInDao {
    private val items = MutableStateFlow<List<CheckIn>>(emptyList())

    override suspend fun insert(checkIn: CheckIn) {
        items.value = items.value + checkIn
    }

    override fun getCheckInsSince(sinceMs: Long): Flow<List<CheckIn>> =
        items.map { list ->
            list.filter { it.timestampMillis >= sinceMs }.sortedByDescending { it.timestampMillis }
        }

    override fun countCheckInsBetween(startMs: Long, endMs: Long): Flow<Int> =
        items.map { list -> list.count { it.timestampMillis in startMs until endMs } }

    override suspend fun getDistinctCheckInDays(): List<String> =
        items.value
            .map { Instant.ofEpochMilli(it.timestampMillis).atZone(ZoneId.systemDefault()).toLocalDate().toString() }
            .distinct()
            .sortedDescending()
}

/** In-memory LessonDao for JVM unit tests (insertAll ignores existing ids, like Room IGNORE). */
class FakeLessonDao : LessonDao {
    private val items = MutableStateFlow<List<Lesson>>(emptyList())

    override suspend fun insertAll(lessons: List<Lesson>) {
        val existingIds = items.value.map { it.id }.toSet()
        items.value = items.value + lessons.filter { it.id !in existingIds }
    }

    override fun getAllLessons(): Flow<List<Lesson>> =
        items.map { list -> list.sortedBy { it.orderIndex } }

    override fun getLessonById(lessonId: String): Flow<Lesson?> =
        items.map { list -> list.firstOrNull { it.id == lessonId } }

    override suspend fun markCompleted(lessonId: String, completedAtMs: Long) {
        items.value = items.value.map {
            if (it.id == lessonId) it.copy(completedAt = completedAtMs) else it
        }
    }

    override fun getCompletedCount(): Flow<Int> =
        items.map { list -> list.count { it.completedAt != null } }
}

/** In-memory GoalDao for JVM unit tests; upsert replaces on the (appId, weekStartDate) key. */
class FakeGoalDao : GoalDao {
    private val items = MutableStateFlow<List<Goal>>(emptyList())

    override suspend fun upsert(goal: Goal) {
        items.value = items.value
            .filterNot { it.appId == goal.appId && it.weekStartDate == goal.weekStartDate } + goal
    }

    override fun getGoalsForWeek(weekStart: LocalDate): Flow<List<Goal>> =
        items.map { list -> list.filter { it.weekStartDate == weekStart } }

    override suspend fun updateProgress(goalId: String, progressMinutes: Int) {
        items.value = items.value.map {
            if (it.id == goalId) it.copy(weekProgress = progressMinutes) else it
        }
    }
}
