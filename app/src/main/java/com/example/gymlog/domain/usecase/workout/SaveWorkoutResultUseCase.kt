package com.example.gymlog.domain.usecase.workout

import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import javax.inject.Inject

class SaveWorkoutResultUseCase @Inject constructor(
    private val repositoryInterface: WorkoutRepositoryInterface
) {
    suspend operator fun invoke(result: WorkoutResult) {
        repositoryInterface.saveWorkoutResult(result)
    }
}