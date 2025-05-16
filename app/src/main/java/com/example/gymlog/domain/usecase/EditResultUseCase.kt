package com.example.gymlog.domain.usecase


import com.example.gymlog.domain.model.plan.GymDayNew
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import javax.inject.Inject


class EditResultUseCase @Inject constructor(
    private val workoutResultRepositoryInterface: WorkoutResultRepositoryInterface,
    private val getGymDayWithResultsUseCase: GetGymDayWithResultsUseCase
) {
    suspend operator fun invoke(
        idResult: Long?,
        programUuid: String,
        gymDayId: Long,
        maxResultsPerExercise: Int,
        workoutDateTime: String,
        weight: Int?,
        iterations: Int?,
        workTime: Int?,
    ): GymDayNew {

        // 1. Оновлюємо результат через репозиторій
        workoutResultRepositoryInterface.updateResultById(idResult, weight, iterations, workTime)

        // 3. Отримуємо оновлений список результатів для цієї вправи
        return getGymDayWithResultsUseCase(
            programUuid = programUuid,
            gymDayId = gymDayId,
            maxResultsPerExercise = maxResultsPerExercise,
            currentWorkoutDateTime = workoutDateTime
        )

    }
}