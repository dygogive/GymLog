// WorkoutUiState.kt
package com.example.gymlog.presentation.state

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * UI-стан екрану тренування, розділений на логічні групи
 */
data class WorkoutUiState(
    val timerState: TimerState = TimerState(),
    val trainingState: TrainingState = TrainingState(),
    val selectionState: SelectionState = SelectionState(),
    val resultsState: ResultsState = ResultsState()
)

/**
 * Стан таймерів для контролю тренування
 */
data class TimerState(
    val totalTimeMs: Long = 0L,
    val lastSetTimeMs: Long = 0L,
    val isGymRunning: Boolean = false
)

/**
 * Стан тренувальних блоків і вправ
 */
data class TrainingState(
    val blocks: PersistentList<TrainingBlock> = persistentListOf(),
    val isWorkoutActive: Boolean = false
)

/**
 * Стан діалогу вибору програми та дня тренування
 */
data class SelectionState(
    val availablePrograms: PersistentList<FitnessProgram> = persistentListOf(),
    val selectedProgram: FitnessProgram? = null,
    val availableGymDaySessions: PersistentMap<Long, PersistentList<GymDay>> = persistentMapOf(),
    val selectedGymDay: GymDay? = null,
    val showSelectionDialog: Boolean = true
)

/**
 * Стан збережених результатів тренування
 * Key: ID вправи, Value: список результатів для цієї вправи
 */
data class ResultsState(
    val workoutResults: PersistentMap<Long, PersistentList<WorkoutResult>> = persistentMapOf()
)