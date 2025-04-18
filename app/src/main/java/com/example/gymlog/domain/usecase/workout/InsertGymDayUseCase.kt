// domain/usecase/InsertGymDayUseCase.kt
package com.example.gymlog.domain.usecase.workout

import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import javax.inject.Inject

/**
 * Додає новий тренувальний день у базу.
 */
class InsertGymDayUseCase @Inject constructor(
    private val repository: WorkoutRepositoryInterface
) {
    suspend operator fun invoke(day: WorkoutGymDayEntity): Long =
        repository.insertWorkGymDay(day)
}
