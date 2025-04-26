package com.example.gymlog.domain.repository


import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.domain.model.workout.WorkoutGymDay
import com.example.gymlog.domain.model.workout.WorkoutResult
import kotlinx.coroutines.flow.Flow

interface WorkoutRepositoryInterface {
    suspend fun insertWorkGymDay(day: WorkoutGymDay): Long

    fun getAllWorkGymDays(): Flow<List<WorkoutGymDay>>

    suspend fun getLastThreeFirstResults(exerciseId: Long): List<WorkoutExercise>

    suspend fun saveWorkoutResult(result: WorkoutResult)
}