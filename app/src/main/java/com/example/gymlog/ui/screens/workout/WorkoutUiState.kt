package com.example.gymlog.ui.screens.workout

import com.example.gymlog.database.room.WorkoutSet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


data class WorkoutUiState(
    val totalTimeMs: Long = 0L,
    val restTimeMs: Long = 0L,

    val sets: ImmutableList<WorkoutSet> = persistentListOf<WorkoutSet>()
)
