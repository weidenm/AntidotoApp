package com.antidoto.data.repository

import com.antidoto.data.db.dao.CheckInDao
import com.antidoto.data.db.entities.CheckIn
import com.antidoto.domain.model.Mood
import com.antidoto.domain.model.Trigger
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CheckInRepository @Inject constructor(
    private val checkInDao: CheckInDao,
) {

    suspend fun recordCheckIn(mood: Mood, trigger: Trigger) {
        checkInDao.insert(CheckIn(mood = mood, trigger = trigger))
    }

    fun getCheckInsSince(sinceMs: Long): Flow<List<CheckIn>> =
        checkInDao.getCheckInsSince(sinceMs)

    fun countCheckInsBetween(startMs: Long, endMs: Long): Flow<Int> =
        checkInDao.countCheckInsBetween(startMs, endMs)

    suspend fun getCheckInDays(): List<LocalDate> =
        checkInDao.getDistinctCheckInDays().map(LocalDate::parse)
}
