package com.antidoto.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.antidoto.domain.model.Mood
import com.antidoto.domain.model.Trigger
import java.util.UUID

@Entity(tableName = "check_ins")
data class CheckIn(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val mood: Mood,
    val trigger: Trigger,
    @ColumnInfo(name = "timestamp_millis") val timestampMillis: Long = System.currentTimeMillis(),
)
