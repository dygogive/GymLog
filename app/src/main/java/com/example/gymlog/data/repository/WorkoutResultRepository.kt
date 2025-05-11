package com.example.gymlog.data.repository

import android.util.Log
import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import com.example.gymlog.data.local.room.mappers.toDomain
import com.example.gymlog.data.local.room.mappers.toEntity
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import javax.inject.Inject

class WorkoutResultRepository @Inject constructor(
    private val workoutResultDao: WorkoutResultDao
): WorkoutResultRepositoryInterface {

    override suspend fun getAllResultsForExercise(exerciseInBlockId: Long): List<WorkoutResult> {
        return workoutResultDao.getResultsForExercise(exerciseInBlockId).map { it.toDomain() }
    }


    override suspend fun saveWorkoutResult(result: WorkoutResult) {
        workoutResultDao.insert(result.toEntity())
    }

    override suspend fun deleteResultById(idResult: Long?) {
        Log.d("deleteResultById", "deleteResultById: 1 - $idResult")
        workoutResultDao.deleteById(idResult!!)
        Log.d("deleteResultById", "deleteResultById: 2 - $idResult")
    }
}