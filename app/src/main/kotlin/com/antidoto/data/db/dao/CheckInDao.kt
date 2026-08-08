package com.antidoto.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.antidoto.data.db.entities.CheckIn
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {

    @Insert
    suspend fun insert(checkIn: CheckIn)

    @Query(
        "SELECT id, mood, `trigger`, timestamp_millis FROM check_ins " +
            "WHERE timestamp_millis >= :sinceMs ORDER BY timestamp_millis DESC",
    )
    fun getCheckInsSince(sinceMs: Long): Flow<List<CheckIn>>

    @Query(
        "SELECT COUNT(id) FROM check_ins " +
            "WHERE timestamp_millis >= :startMs AND timestamp_millis < :endMs",
    )
    fun countCheckInsBetween(startMs: Long, endMs: Long): Flow<Int>

    @Query(
        "SELECT DISTINCT date(timestamp_millis / 1000, 'unixepoch', 'localtime') FROM check_ins " +
            "ORDER BY 1 DESC",
    )
    suspend fun getDistinctCheckInDays(): List<String>
}
