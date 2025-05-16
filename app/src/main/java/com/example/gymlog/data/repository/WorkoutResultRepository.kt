package com.example.gymlog.data.repository

import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import com.example.gymlog.data.local.room.mappers.toDomain
import com.example.gymlog.data.local.room.mappers.toEntity
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import javax.inject.Inject

class WorkoutResultRepository @Inject constructor(
    private val workoutResultDao: WorkoutResultDao
): WorkoutResultRepositoryInterface {

    override suspend fun getAllResultsForExercise(
        programUuid: String,
        exerciseId: Long,
        trainingBlockUuid: String?
    ): List<WorkoutResult> {
        return workoutResultDao.getResultsForExercise(
            programUuid = programUuid,
            exerciseId = exerciseId,
            trainingBlockUuid = trainingBlockUuid
        ).map { it.toDomain() }
    }


    override suspend fun saveWorkoutResult(result: WorkoutResult) {
        workoutResultDao.insert(result.toEntity())
    }

    override suspend fun deleteResultById(idResult: Long?) {
        workoutResultDao.deleteById(idResult!!)
    }


    override suspend fun updateResultById(id: Long?, weight: Int?, iteration: Int?, workTime: Int?) {
        workoutResultDao.updateResultFields(id, weight, iteration, workTime)
    }
}