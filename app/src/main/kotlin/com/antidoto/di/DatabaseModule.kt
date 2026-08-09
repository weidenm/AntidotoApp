package com.antidoto.di

import android.content.Context
import androidx.room.Room
import com.antidoto.data.db.AppDatabase
import com.antidoto.data.db.dao.AppUsageEntryDao
import com.antidoto.data.db.dao.CheckInDao
import com.antidoto.data.db.dao.GoalDao
import com.antidoto.data.db.dao.LessonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .build()

    @Provides
    fun provideAppUsageEntryDao(database: AppDatabase): AppUsageEntryDao =
        database.appUsageEntryDao()

    @Provides
    fun provideCheckInDao(database: AppDatabase): CheckInDao = database.checkInDao()

    @Provides
    fun provideLessonDao(database: AppDatabase): LessonDao = database.lessonDao()

    @Provides
    fun provideGoalDao(database: AppDatabase): GoalDao = database.goalDao()
}
