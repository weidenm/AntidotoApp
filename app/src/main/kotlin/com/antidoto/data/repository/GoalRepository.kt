package com.antidoto.data.repository

import com.antidoto.data.db.dao.GoalDao
import com.antidoto.data.db.entities.Goal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
) {

    fun getActiveGoals(): Flow<List<Goal>> = goalDao.getGoalsForWeek(currentWeekStart())

    suspend fun saveGoal(goal: Goal) = goalDao.upsert(goal)

    suspend fun updateProgress(goalId: String, progressMinutes: Int) =
        goalDao.updateProgress(goalId, progressMinutes)

    companion object {
        fun currentWeekStart(today: LocalDate = LocalDate.now()): LocalDate =
            today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }
}
