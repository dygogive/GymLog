package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.plan.FitnessProgramNew
import com.example.gymlog.domain.model.plan.GymDayNew
import com.example.gymlog.domain.model.workout.WorkoutResult

interface FitnessProgramNewRepositoryInterface {
    suspend fun getAllFitnessPrograms(): List<FitnessProgramNew>
    suspend fun getAllGymDays(idProgram: Long): List<GymDayNew>
    suspend fun getSelectedGymDayNew(idGymDay: Long): GymDayNew
    suspend fun getWorkoutResultsForExercise(exerciseInBlockId: Long): List<WorkoutResult>
}