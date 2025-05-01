package com.example.gymlog.domain.usecase.workout


import com.example.gymlog.domain.model.workout.WorkoutGymDay
import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import javax.inject.Inject

class GetWorkoutGymDaysUseCase @Inject constructor(
    private val repository: WorkoutRepositoryInterface
) {
    suspend operator fun invoke(): List<WorkoutGymDay> {
        return repository.getWorkoutGymDays()
    }
}