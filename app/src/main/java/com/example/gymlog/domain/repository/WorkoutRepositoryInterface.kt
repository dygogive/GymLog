package com.example.gymlog.domain.repository

import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import kotlinx.coroutines.flow.Flow

interface WorkoutRepositoryInterface {
    suspend fun insertWorkGymDay(day: WorkoutGymDayEntity): Long
    fun getAllWorkGymDays(): Flow<List<WorkoutGymDayEntity>>
}