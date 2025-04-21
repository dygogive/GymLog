// domain/usecase/InsertGymDayUseCase.kt
package com.example.gymlog.domain.usecase.workout

import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import com.example.gymlog.domain.model.workout.WorkoutGymDay
import javax.inject.Inject

/**
 * Додає новий тренувальний день у базу.
 */
class InsertGymDayUseCase @Inject constructor(
    private val repository: WorkoutRepositoryInterface
) {
    suspend operator fun invoke(day: WorkoutGymDay): Long =
        repository.insertWorkGymDay(day)
}
