package com.example.gymlog.data.repository

import android.util.Log
import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import com.example.gymlog.data.local.room.entities.WorkoutResultEntity
import com.example.gymlog.data.local.room.mappers.toDomain
import com.example.gymlog.data.local.room.mappers.toEntity
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class WorkoutResultRepository @Inject constructor(
    private val workoutResultDao: WorkoutResultDao
): WorkoutResultRepositoryInterface {


    override suspend fun getBestUniqueResults(
        exerciseInBlockId: Long,
        resultsNumber: Int
    ): List<WorkoutResult> {
        val allResults = workoutResultDao.getResultsForExercise(exerciseInBlockId)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

        return allResults
            // Групуємо по workoutDateTime
            .groupBy { it.workoutDateTime }
            // Вибираємо найкращий результат з кожної групи
            .map { (_, results) ->
                results
                    .sortedWith(
                        compareByDescending<WorkoutResultEntity> { it.weight ?: 0 }
                            .thenByDescending { it.iteration ?: 0 }
                            .thenBy { it.workTime ?: Int.MAX_VALUE }
                    )
                    .firstOrNull() ?: throw IllegalStateException("Empty group")
            }
            // Сортуємо по workoutDateTime від нових до старих
            .sortedByDescending {
                try {
                    LocalDateTime.parse(it.workoutDateTime, formatter)
                } catch (e: Exception) {
                    LocalDateTime.MIN
                }
            }
            // Обрізаємо до кількості
            .take(resultsNumber)
            // Мапимо в domain-модель
            .map { it.toDomain() }
    }



    override suspend fun saveWorkoutResult(result: WorkoutResult) {
        workoutResultDao.insert(result.toEntity())
    }
}