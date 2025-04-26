// WorkoutUiState.kt
package com.example.gymlog.presentation.state

import com.example.gymlog.ui.feature.workout.model.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * UI-стан екрану тренування
 */
data class WorkoutUiState(
    val timerState: TimerState = TimerState(),
    val trainingState: TrainingState = TrainingState(),
    val selectionState: SelectionState = SelectionState(),
    val resultsState: ResultsState = ResultsState()
)

/** Timer UI state */
data class TimerState(
    val totalTimeMs: Long = 0L,
    val lastSetTimeMs: Long = 0L,
    val isGymRunning: Boolean = false
)

/** Training blocks UI state */
data class TrainingState(
    val blocks: PersistentList<TrainingBlockUiModel> = persistentListOf(),
    val isWorkoutActive: Boolean = false
)

/** Selection dialog UI state */
data class SelectionState(
    val availablePrograms: PersistentList<ProgramInfo> = persistentListOf(),
    val selectedProgram: ProgramInfo? = null,
    val availableGymDaySessions: PersistentMap<ProgramInfo, PersistentList<GymDayUiModel>> = persistentMapOf(),
    val selectedGymDay: GymDayUiModel? = null,
    val showSelectionDialog: Boolean = true
)

/** Workout results UI state */
data class ResultsState(
    val workoutResults: PersistentMap<Long, PersistentList<ResultOfSet>> = persistentMapOf()
)
