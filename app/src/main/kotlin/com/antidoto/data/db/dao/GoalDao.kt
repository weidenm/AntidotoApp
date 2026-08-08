package com.antidoto.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.antidoto.data.db.entities.Goal
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goal: Goal)

    @Query(
        "SELECT id, appId, weekStartDate, targetMinutes, userSetMinutes, weekProgress " +
            "FROM goals WHERE weekStartDate = :weekStart",
    )
    fun getGoalsForWeek(weekStart: LocalDate): Flow<List<Goal>>

    @Query("UPDATE goals SET weekProgress = :progressMinutes WHERE id = :goalId")
    suspend fun updateProgress(goalId: String, progressMinutes: Int)
}
