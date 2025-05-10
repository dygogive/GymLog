package com.example.gymlog.domain.usecase


import com.example.gymlog.domain.model.plan.GymDayNew
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import javax.inject.Inject


class SaveResultUseCase @Inject constructor(
    private val workoutResultRepositoryInterface: WorkoutResultRepositoryInterface,
    private val getGymDayWithResultsUseCase: GetGymDayWithResultsUseCase
) {
    suspend operator fun invoke(
        gymDayId: Long,
        maxResultsPerExercise: Int,
        exerciseInBlockId: Long,
        weight: Int?,
        iterations: Int?,
        workTime: Int?,
        sequenceInGymDay: Int,
        timeFromStart: Long,
        workoutDateTime: String
    ): GymDayNew {
        // 1. Створюємо новий результат тренування
        val result = WorkoutResult(
            id = null, // Авто-генерація ID в базі даних
            exerciseInBlockId = exerciseInBlockId,
            weight = weight,
            iteration = iterations,
            workTime = workTime,
            position = 0, // Буде оновлено пізніше
            sequenceInGymDay = sequenceInGymDay,
            timeFromStart = timeFromStart,
            workoutDateTime = workoutDateTime
        )

        // 2. Зберігаємо результат через репозиторій
        workoutResultRepositoryInterface.saveWorkoutResult(result)

        // 3. Отримуємо оновлений список результатів для цієї вправи
        return getGymDayWithResultsUseCase(
            gymDayId = gymDayId,
            maxResultsPerExercise = maxResultsPerExercise,
            currentWorkoutDateTime = workoutDateTime
        )
    }
}