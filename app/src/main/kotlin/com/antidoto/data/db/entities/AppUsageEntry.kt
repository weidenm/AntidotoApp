package com.antidoto.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "app_usage_entries",
    indices = [Index(value = ["date"]), Index(value = ["packageName", "date"])],
)
data class AppUsageEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val appName: String,
    val durationMs: Long,
    val openCount: Int,
    @ColumnInfo(name = "timestamp_millis") val timestampMillis: Long = System.currentTimeMillis(),
    val date: LocalDate,
)
