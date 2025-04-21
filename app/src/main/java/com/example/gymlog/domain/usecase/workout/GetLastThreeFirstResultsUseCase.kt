package com.example.gymlog.domain.usecase.workout

import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import javax.inject.Inject

class GetLastThreeFirstResultsUseCase @Inject constructor(
    private val repositoryInterface: WorkoutRepositoryInterface
) {
    suspend operator fun invoke(exerciseId: Long): List<WorkoutExercise> {
        return repositoryInterface.getLastThreeFirstResults(exerciseId)
    }
}