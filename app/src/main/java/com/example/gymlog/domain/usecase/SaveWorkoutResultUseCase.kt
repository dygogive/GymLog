package com.example.gymlog.domain.usecase

import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import javax.inject.Inject

// UseCase для збереження результату
class SaveWorkoutResultUseCase @Inject constructor(
    private val repository: FitnessProgramNewRepositoryInterface
) {
    suspend operator fun invoke(result: WorkoutResult) {
        repository.saveWorkoutResult(result)
    }
}