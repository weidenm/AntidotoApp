package com.antidoto.data.repository

import com.antidoto.data.db.entities.Goal
import com.antidoto.testutil.FakeGoalDao
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalRepositoryTest {

    @Test
    fun `week start on monday returns same day`() {
        val monday = LocalDate.of(2026, 7, 6)

        assertEquals(monday, GoalRepository.currentWeekStart(monday))
    }

    @Test
    fun `active goals reflect the current week and upsert replaces per app`() = runTest {
        val repo = GoalRepository(FakeGoalDao())
        val week = GoalRepository.currentWeekStart()

        repo.saveGoal(Goal(appId = "com.instagram.android", weekStartDate = week, targetMinutes = 60, userSetMinutes = 60))
        assertEquals(1, repo.getActiveGoals().first().size)

        // Upsert same app/week replaces rather than duplicating.
        repo.saveGoal(Goal(appId = "com.instagram.android", weekStartDate = week, targetMinutes = 30, userSetMinutes = 30))
        val goals = repo.getActiveGoals().first()
        assertEquals(1, goals.size)
        assertEquals(30, goals.first().targetMinutes)
    }

    @Test
    fun `updateProgress changes the stored week progress`() = runTest {
        val repo = GoalRepository(FakeGoalDao())
        val week = GoalRepository.currentWeekStart()
        val goal = Goal(appId = "com.tiktok", weekStartDate = week, targetMinutes = 90, userSetMinutes = 90)
        repo.saveGoal(goal)

        repo.updateProgress(goal.id, 45)

        assertEquals(45, repo.getActiveGoals().first().first().weekProgress)
    }

    @Test
    fun `week start mid week returns previous monday`() {
        val friday = LocalDate.of(2026, 7, 10)

        assertEquals(LocalDate.of(2026, 7, 6), GoalRepository.currentWeekStart(friday))
    }

    @Test
    fun `week start on sunday returns previous monday`() {
        val sunday = LocalDate.of(2026, 7, 12)

        assertEquals(LocalDate.of(2026, 7, 6), GoalRepository.currentWeekStart(sunday))
    }
}
