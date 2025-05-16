package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.workout.WorkoutResult

interface WorkoutResultRepositoryInterface {

    suspend fun getAllResultsForExercise(
        programUuid: String,
        exerciseId: Long,
        trainingBlockUuid: String? = null
    ): List<WorkoutResult>


    suspend fun saveWorkoutResult(result: WorkoutResult)

    //Delete result in database
    suspend fun deleteResultById(idResult: Long?)

    suspend fun updateResultById(id: Long?, weight: Int?, iteration: Int?, workTime: Int?)
}