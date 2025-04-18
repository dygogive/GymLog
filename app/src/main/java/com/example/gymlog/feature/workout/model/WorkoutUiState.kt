package com.example.gymlog.feature.workout.model

import com.example.gymlog.domain.model.plan.TrainingBlock
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class WorkoutUiState(
    val totalTimeMs: Long = 0L,
    var lastSetTimeMs: Long = 0L,
    val blocks: PersistentList<TrainingBlock> = persistentListOf<TrainingBlock>(),
    val isGymRunning: Boolean = false,      // Чи активне тренування
) {



}