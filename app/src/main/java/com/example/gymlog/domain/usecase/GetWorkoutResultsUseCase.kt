package com.example.gymlog.domain.usecase

import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import javax.inject.Inject

// UseCase для отримання результатів
class GetWorkoutResultsUseCase @Inject constructor(
    private val repository: FitnessProgramNewRepositoryInterface
) {
    suspend operator fun invoke(
        exerciseInBlockId: Long,
        currentWorkoutDateTime: String?
    ): Pair<List<WorkoutResult>, List<WorkoutResult>> {
        val current = currentWorkoutDateTime?.let {
            repository.getCurrentWorkoutResults(exerciseInBlockId, it)
        } ?: emptyList()

        val historical = repository.getLatestResultsForExercise(exerciseInBlockId)
        return current to historical
    }
}