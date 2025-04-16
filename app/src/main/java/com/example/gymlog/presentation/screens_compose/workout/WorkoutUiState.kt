package com.example.gymlog.presentation.screens_compose.workout

import com.example.gymlog.data.local.room.entity.TrainingBlockEntity
import com.example.gymlog.domain.model.plan.TrainingBlock
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf


data class WorkoutUiState(
    val totalTimeMs: Long = 0L,
    val lastSetTimeMs: Long = 0L,
    val blocks: PersistentList<TrainingBlock> = persistentListOf<TrainingBlock>(),
    val isGymRunning: Boolean = false,      // Чи активне тренування
) {



}
