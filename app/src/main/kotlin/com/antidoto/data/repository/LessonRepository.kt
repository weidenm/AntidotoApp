package com.antidoto.data.repository

import com.antidoto.data.db.dao.LessonDao
import com.antidoto.data.db.entities.Lesson
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class LessonRepository @Inject constructor(
    private val lessonDao: LessonDao,
) {

    fun getAllLessons(): Flow<List<Lesson>> = lessonDao.getAllLessons()

    fun getLesson(id: String): Flow<Lesson?> = lessonDao.getLessonById(id)

    fun getCompletedCount(): Flow<Int> = lessonDao.getCompletedCount()

    suspend fun seedLessons(lessons: List<Lesson>) = lessonDao.insertAll(lessons)

    suspend fun markCompleted(lessonId: String) =
        lessonDao.markCompleted(lessonId, System.currentTimeMillis())
}
