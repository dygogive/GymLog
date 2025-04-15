package com.example.gymlog.presentation.screens_compose.workout

import com.example.gymlog.data.local.room.entity.TrainingBlockEntity
import com.example.gymlog.domain.model.plan.TrainingBlock
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf


data class WorkoutUiState(
    val totalTimeMs: Long = 0L,
    val lastSetTimeMs: Long = 0L,
    val blocks: PersistentList<TrainingBlock> = persistentListOf<TrainingBlock>(),

    // Додаткові можливі поля:
    val isGymRunning: Boolean = false,      // Чи активне тренування
    val currentTrainingBlockId: Long? = null,         // Поточний активний блок
    val error: String? = null,              // Помилки
    val isLoading: Boolean = false          // Стан завантаження
) {
    // функція бере список  і функцією find, перебираючи кожен (it) знаходить потрібний, у якому id == currentSetId
    fun getCurrentTrainBlock(): TrainingBlock? = blocks.find { it.id == currentTrainingBlockId }
}
