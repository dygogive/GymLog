// domain/usecase/InsertTrainingBlockUseCase.kt
package com.example.gymlog.domain.usecase

import com.example.gymlog.data.local.room.entity.plan.TrainingBlockEntity
import com.example.gymlog.domain.repository.TrainingBlockRepositoryInterface
import javax.inject.Inject

/**
 * Юзкейс для додавання нового тренувального блоку.
 */
class InsertTrainingBlockUseCase @Inject constructor(
    private val repository: TrainingBlockRepositoryInterface
) {
    suspend operator fun invoke(block: TrainingBlockEntity): Long =
        repository.insertTrainingBlock(block)
}
