package com.example.gymlog.presentation.state

import com.example.gymlog.domain.model.plan.FitnessProgram
import com.example.gymlog.domain.model.plan.Gym
import com.example.gymlog.domain.model.plan.TrainingBlock
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf


data class WorkoutUiState(
    val totalTimeMs: Long = 0L,
    var lastSetTimeMs: Long = 0L,
    val blocks: PersistentList<TrainingBlock> = persistentListOf<TrainingBlock>(),
    val isGymRunning: Boolean = false,      // Чи активне тренування


    //для діалогу вибору
    val availablePrograms: PersistentList<FitnessProgram> = persistentListOf(),
    val selectedProgram: FitnessProgram? = null,
    val availableGymSessions: PersistentMap<Long, List<Gym>> = persistentMapOf(),
    val selectedGym: Gym? = null,

    // нове: чи показувати діалог
    val showSelectionDialog: Boolean = true
)