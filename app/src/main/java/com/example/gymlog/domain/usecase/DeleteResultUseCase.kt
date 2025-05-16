package com.example.gymlog.domain.usecase


import android.util.Log
import com.example.gymlog.domain.model.plan.GymDayNew
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import javax.inject.Inject


class DeleteResultUseCase @Inject constructor(
    private val workoutResultRepositoryInterface: WorkoutResultRepositoryInterface,
    private val getGymDayWithResultsUseCase: GetGymDayWithResultsUseCase
) {
    suspend operator fun invoke(
        idResult: Long?,
        gymDayId: Long,
        maxResultsPerExercise: Int,
        workoutDateTime: String
    ): GymDayNew {
        // 1. Видалимо результат через репозиторій
        workoutResultRepositoryInterface.deleteResultById(idResult)
        // 2. Отримуємо оновлену програму
        return getGymDayWithResultsUseCase(
            gymDayId = gymDayId,
            maxResultsPerExercise = maxResultsPerExercise,
            currentWorkoutDateTime = workoutDateTime
        )
    }
}