package com.example.gymlog.domain.usecase

import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetBestUniqueResultsUseCase @Inject constructor(
    private val workoutResultRepository: WorkoutResultRepositoryInterface
) {
    suspend operator fun invoke(
        exerciseInBlockId: Long,
        resultsNumber: Int,
        currentWorkoutDateTime: String?
    ): List<WorkoutResult> {
        val allResults = workoutResultRepository.getAllResultsForExercise(exerciseInBlockId)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

        val filterResults = allResults.filter { it.workoutDateTime == currentWorkoutDateTime }

        if (filterResults.isNotEmpty()) {
            return filterResults.sortedWith(
                compareByDescending<WorkoutResult> { it.weight }
                    .thenByDescending { it.iteration }
                    .thenBy { it.workTime }
            )
        } else return allResults
            // Group by workoutDateTime
            .groupBy { it.workoutDateTime }
            // Select the best result from each group
            .map { (_, results) ->
                results
                    .sortedWith(
                        compareByDescending<WorkoutResult> { it.weight ?: 0 }
                            .thenByDescending { it.iteration ?: 0 }
                            .thenBy { it.workTime ?: Int.MAX_VALUE }
                    )
                    .firstOrNull() ?: throw IllegalStateException("Empty group")
            }
            // Sort by workoutDateTime from newest to oldest
            .sortedByDescending {
                try {
                    LocalDateTime.parse(it.workoutDateTime, formatter)
                } catch (e: Exception) {
                    LocalDateTime.MIN
                }
            }
            // Limit to requested number

    }
}