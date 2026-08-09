package com.antidoto.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.antidoto.data.db.entities.AppUsageEntry
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageEntryDao {

    @Query(
        "SELECT id, packageName, appName, durationMs, openCount, timestamp_millis, date " +
            "FROM app_usage_entries WHERE date = :date ORDER BY durationMs DESC",
    )
    fun getEntriesForDate(date: LocalDate): Flow<List<AppUsageEntry>>

    @Query(
        "SELECT id, packageName, appName, durationMs, openCount, timestamp_millis, date " +
            "FROM app_usage_entries WHERE date BETWEEN :start AND :end ORDER BY date ASC",
    )
    fun getEntriesBetween(start: LocalDate, end: LocalDate): Flow<List<AppUsageEntry>>

    @Query(
        "SELECT id, packageName, appName, durationMs, openCount, timestamp_millis, date " +
            "FROM app_usage_entries WHERE packageName = :packageName AND date BETWEEN :start AND :end " +
            "ORDER BY date ASC",
    )
    fun getEntriesForApp(packageName: String, start: LocalDate, end: LocalDate): Flow<List<AppUsageEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<AppUsageEntry>)

    @Query("DELETE FROM app_usage_entries WHERE date = :date")
    suspend fun deleteForDate(date: LocalDate)

    @Transaction
    suspend fun replaceForDate(date: LocalDate, entries: List<AppUsageEntry>) {
        deleteForDate(date)
        insertAll(entries)
    }
}
