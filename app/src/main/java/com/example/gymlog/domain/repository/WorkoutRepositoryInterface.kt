package com.example.gymlog.domain.repository

import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import kotlinx.coroutines.flow.Flow

interface WorkoutRepositoryInterface {
    suspend fun insertGymDay(day: WorkoutGymDayEntity): Long
    fun getAllGymDays(): Flow<List<WorkoutGymDayEntity>>
}