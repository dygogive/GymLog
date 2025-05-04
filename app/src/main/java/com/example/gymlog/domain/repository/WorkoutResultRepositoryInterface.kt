package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.workout.WorkoutResult

interface WorkoutResultRepositoryInterface {
    suspend fun getBestUniqueResults(
        exerciseInBlockId: Long,
        resultsNumber: Int = 3
    ): List<WorkoutResult>
}