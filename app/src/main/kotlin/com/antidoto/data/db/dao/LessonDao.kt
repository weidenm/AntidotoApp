package com.antidoto.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.antidoto.data.db.entities.Lesson
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(lessons: List<Lesson>)

    @Query(
        "SELECT id, title, content, imageUrl, completedAt, orderIndex FROM lessons " +
            "ORDER BY orderIndex ASC",
    )
    fun getAllLessons(): Flow<List<Lesson>>

    @Query("UPDATE lessons SET completedAt = :completedAtMs WHERE id = :lessonId")
    suspend fun markCompleted(lessonId: String, completedAtMs: Long)

    @Query("SELECT COUNT(id) FROM lessons WHERE completedAt IS NOT NULL")
    fun getCompletedCount(): Flow<Int>
}
