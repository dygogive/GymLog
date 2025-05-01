package com.example.gymlog.domain.usecase.workout

import com.example.gymlog.domain.model.workout.WorkoutGymDay
import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import javax.inject.Inject

class SaveWorkoutGymDayUseCase @Inject constructor(
    private val repositoryInterface: WorkoutRepositoryInterface
) {
    suspend operator fun invoke(workoutGymDay: WorkoutGymDay) {
        repositoryInterface.updateWorkoutGymDay(workoutGymDay)
    }
}