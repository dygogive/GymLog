// domain/usecase/InsertTrainingBlockUseCase.kt
package com.example.gymlog.domain.usecase


import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.domain.repository.TrainingBlockRepositoryInterface
import javax.inject.Inject

/**
 * Юзкейс для додавання нового тренувального блоку.
 */
class InsertTrainingBlockUseCase @Inject constructor(
    private val repository: TrainingBlockRepositoryInterface
) {
    suspend operator fun invoke(block: TrainingBlock): Long =
        repository.insertTrainingBlock(block)
}
