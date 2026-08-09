package com.antidoto.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val completedAt: Long? = null,
    val orderIndex: Int,
)
