package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.workout.WorkoutResult

interface WorkoutResultRepositoryInterface {

    suspend fun getAllResultsForExercise(exerciseInBlockId: Long): List<WorkoutResult>


    suspend fun saveWorkoutResult(result: WorkoutResult)

    //Delete result in database
    suspend fun deleteResultById(idResult: Long?)
}