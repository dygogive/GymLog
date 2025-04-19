// domain/usecase/GetTrainingBlocksByDayIdUseCase.kt
package com.example.gymlog.domain.usecase

import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.domain.repository.TrainingBlockRepositoryInterface
import javax.inject.Inject

/**
 * Юзкейс для отримання доменних моделей TrainingBlock (зі всіма фільтрами й вправами)
 * в однеразовому виклику по gymDayId.
 */
class GetTrainingBlocksByDayIdUseCase @Inject constructor(
    private val repository: TrainingBlockRepositoryInterface
) {
    suspend operator fun invoke(gymDayId: Long): List<TrainingBlock> =
        repository.getTrainingBlocksByDayId(gymDayId)
}
