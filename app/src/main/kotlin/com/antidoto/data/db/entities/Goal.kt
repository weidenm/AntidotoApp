package com.antidoto.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "goals",
    indices = [Index(value = ["appId", "weekStartDate"], unique = true)],
)
data class Goal(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val appId: String,
    val weekStartDate: LocalDate,
    val targetMinutes: Int,
    val userSetMinutes: Int,
    val weekProgress: Int = 0,
)
