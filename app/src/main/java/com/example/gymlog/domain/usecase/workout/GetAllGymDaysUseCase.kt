// domain/usecase/GetAllGymDaysUseCase.kt
package com.example.gymlog.domain.usecase.workout

import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import com.example.gymlog.domain.model.workout.WorkoutGymDay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Повертає стрім усіх тренувальних днів.
 */
class GetAllGymDaysUseCase @Inject constructor(
    private val repository: WorkoutRepositoryInterface
) {
    operator fun invoke(): Flow<List<WorkoutGymDay>> =
        repository.getAllWorkGymDays()
}
