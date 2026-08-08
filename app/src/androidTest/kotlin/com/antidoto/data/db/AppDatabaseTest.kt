package com.antidoto.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.antidoto.data.db.entities.AppUsageEntry
import com.antidoto.data.db.entities.CheckIn
import com.antidoto.data.db.entities.Goal
import com.antidoto.data.db.entities.Lesson
import com.antidoto.domain.model.Mood
import com.antidoto.domain.model.Trigger
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun checkIn_roundTrips_withEnumConverters() = runTest(timeout = 60.seconds) {
        val dao = db.checkInDao()
        dao.insert(CheckIn(mood = Mood.ANXIOUS, trigger = Trigger.HABIT, timestampMillis = 1_000L))

        val checkIns = dao.getCheckInsSince(0).first()
        assertEquals(1, checkIns.size)
        assertEquals(Mood.ANXIOUS, checkIns.first().mood)
        assertEquals(Trigger.HABIT, checkIns.first().trigger)
    }

    @Test
    fun lessons_insertIgnoresDuplicates_andTracksCompletion() = runTest(timeout = 60.seconds) {
        val dao = db.lessonDao()
        dao.insertAll(listOf(Lesson(id = "L0", title = "a", content = "c", orderIndex = 0)))
        dao.insertAll(listOf(Lesson(id = "L0", title = "changed", content = "c2", orderIndex = 0)))

        assertEquals(1, dao.getAllLessons().first().size)
        assertEquals("a", dao.getLessonById("L0").first()?.title)

        dao.markCompleted("L0", 5_000L)
        assertEquals(1, dao.getCompletedCount().first())
    }

    @Test
    fun appUsage_replaceForDate_swapsTheDaySnapshot() = runTest(timeout = 60.seconds) {
        val dao = db.appUsageEntryDao()
        val date = LocalDate.of(2026, 8, 8)
        dao.insertAll(listOf(AppUsageEntry(packageName = "a", appName = "A", durationMs = 100, openCount = 1, date = date)))

        dao.replaceForDate(
            date,
            listOf(
                AppUsageEntry(packageName = "b", appName = "B", durationMs = 200, openCount = 2, date = date),
                AppUsageEntry(packageName = "c", appName = "C", durationMs = 300, openCount = 3, date = date),
            ),
        )

        val entries = dao.getEntriesForDate(date).first()
        assertEquals(2, entries.size)
        assertEquals(setOf("b", "c"), entries.map { it.packageName }.toSet())
    }

    @Test
    fun goals_upsert_replacesOnAppAndWeekUniqueIndex() = runTest(timeout = 60.seconds) {
        val dao = db.goalDao()
        val week = LocalDate.of(2026, 8, 3)
        dao.upsert(Goal(appId = "com.app", weekStartDate = week, targetMinutes = 60, userSetMinutes = 60))
        dao.upsert(Goal(appId = "com.app", weekStartDate = week, targetMinutes = 30, userSetMinutes = 30))

        val goals = dao.getGoalsForWeek(week).first()
        assertEquals(1, goals.size)
        assertEquals(30, goals.first().targetMinutes)
    }
}
