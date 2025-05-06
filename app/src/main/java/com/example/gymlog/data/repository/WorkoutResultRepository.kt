package com.example.gymlog.data.repository

import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import com.example.gymlog.data.local.room.entities.WorkoutResultEntity
import com.example.gymlog.data.local.room.mappers.toDomain
import com.example.gymlog.data.local.room.mappers.toEntity
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import javax.inject.Inject

class WorkoutResultRepository @Inject constructor(
    private val workoutResultDao: WorkoutResultDao
): WorkoutResultRepositoryInterface {


    override suspend fun getBestUniqueResults(
        exerciseInBlockId: Long,
        resultsNumber: Int
    ): List<WorkoutResult> {
        val allResults = workoutResultDao.getResultsForExercise(exerciseInBlockId)

        return allResults
            // Групуємо за датою тренування
            .groupBy { it.workoutDateTime }
            // Для кожної дати беремо результат з максимальною вагою
            .map { (_, results) ->
                results.maxWithOrNull(
                    compareBy(
                        { it.weight ?: 0 },
                        { it.iteration ?: 0 }
                    )
                ) ?: throw IllegalStateException("Empty group")
            }
            // Сортуємо за вагою та ітераціями
            .sortedWith(
                compareByDescending<WorkoutResultEntity> { it.weight ?: 0 }
                    .thenByDescending { it.iteration ?: 0 }
            )
            // Обмежуємо кількість результатів
            .take(resultsNumber)
            // Конвертуємо в domain-модель
            .map { it.toDomain() }
    }



    override suspend fun saveWorkoutResult(result: WorkoutResult) {
        workoutResultDao.insert(result.toEntity())
    }
}