package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.workout.WorkoutResult

interface WorkoutResultRepositoryInterface {
    suspend fun getBestUniqueResults(
        exerciseInBlockId: Long,
        resultsNumber: Int
    ): List<WorkoutResult>
    suspend fun saveWorkoutResult(workoutResult: WorkoutResult)
}