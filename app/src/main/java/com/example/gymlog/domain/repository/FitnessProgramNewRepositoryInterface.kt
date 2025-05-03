package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.plan.FitnessProgramNew
import com.example.gymlog.domain.model.workout.WorkoutResult

interface FitnessProgramNewRepositoryInterface {
    suspend fun getAllFitnessProgramsNew(): List<FitnessProgramNew>
    suspend fun saveWorkoutResult(result: WorkoutResult)
    suspend fun getLatestResultsForExercise(exerciseInBlockId: Long, limit: Int = 3): List<WorkoutResult>
    suspend fun getCurrentWorkoutResults(exerciseInBlockId: Long, workoutDateTime: String): List<WorkoutResult>
}