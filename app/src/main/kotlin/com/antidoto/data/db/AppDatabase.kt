package com.antidoto.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.antidoto.data.db.dao.AppUsageEntryDao
import com.antidoto.data.db.dao.CheckInDao
import com.antidoto.data.db.dao.GoalDao
import com.antidoto.data.db.dao.LessonDao
import com.antidoto.data.db.entities.AppUsageEntry
import com.antidoto.data.db.entities.CheckIn
import com.antidoto.data.db.entities.Goal
import com.antidoto.data.db.entities.Lesson

@Database(
    entities = [
        AppUsageEntry::class,
        CheckIn::class,
        Lesson::class,
        Goal::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appUsageEntryDao(): AppUsageEntryDao
    abstract fun checkInDao(): CheckInDao
    abstract fun lessonDao(): LessonDao
    abstract fun goalDao(): GoalDao

    companion object {
        const val DATABASE_NAME = "antidoto.db"
    }
}
