package com.antidoto.data.repository

import com.antidoto.data.db.entities.CheckIn
import com.antidoto.domain.model.Mood
import com.antidoto.domain.model.Trigger
import com.antidoto.testutil.FakeCheckInDao
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CheckInRepositoryTest {

    private fun msFor(date: LocalDate, hour: Int): Long =
        date.atTime(hour, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    @Test
    fun `observeCheckInDays collapses same-day check-ins into one distinct date`() = runTest {
        val dao = FakeCheckInDao()
        val repo = CheckInRepository(dao)
        val today = LocalDate.now()

        dao.insert(CheckIn(mood = Mood.WELL, trigger = Trigger.HABIT, timestampMillis = msFor(today, 9)))
        dao.insert(CheckIn(mood = Mood.TIRED, trigger = Trigger.WORK, timestampMillis = msFor(today, 18)))
        dao.insert(CheckIn(mood = Mood.NEUTRAL, trigger = Trigger.BOREDOM, timestampMillis = msFor(today.minusDays(1), 12)))

        val days = repo.observeCheckInDays().first()

        assertEquals(setOf(today, today.minusDays(1)), days.toSet())
        assertEquals(2, days.size)
    }

    @Test
    fun `recordCheckIn persists a check-in`() = runTest {
        val dao = FakeCheckInDao()
        val repo = CheckInRepository(dao)

        repo.recordCheckIn(Mood.ANXIOUS, Trigger.ANXIETY)

        assertEquals(1, repo.getCheckInsSince(0).first().size)
    }
}
