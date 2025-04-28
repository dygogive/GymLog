// WorkoutUiState.kt
package com.example.gymlog.presentation.state

import com.example.gymlog.ui.feature.workout.model.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * Загальний UI-стан екрану тренування
 *
 * Включає всі компоненти стану для різних частин екрану тренування:
 * - таймера
 * - блоків тренування
 * - діалогу вибору програми
 * - результатів тренувань
 */
data class WorkoutUiState(
    val timerState: TimerState = TimerState(),
    val trainingBlocksState: TrainingBlocksState = TrainingBlocksState(),
    val selectionState: SelectionState = SelectionState(),
    val resultsState: ResultsState = ResultsState()
)

/**
 * Стан таймера тренування
 *
 * Відстежує час всього тренування та поточного підходу
 */
data class TimerState(
    val totalTimeMs: Long = 0L,        // Загальний час тренування в мілісекундах
    val lastSetTimeMs: Long = 0L,      // Час поточного підходу в мілісекундах
    val isGymRunning: Boolean = false  // Чи активне тренування зараз
) {
    /**
     * Загальний час у форматі "години:хвилини:секунди"
     */
    val formattedTotalTime: String
        get() = formatTime(totalTimeMs)

    /**
     * Час підходу у форматі "хвилини:секунди"
     */
    val formattedSetTime: String
        get() = formatTime(lastSetTimeMs, includeHours = false)

    /**
     * Допоміжна функція для форматування часу
     */
    private fun formatTime(timeMs: Long, includeHours: Boolean = true): String {
        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / (1000 * 60)) % 60
        val hours = (timeMs / (1000 * 60 * 60))

        return if (includeHours && hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}

/**
 * Стан блоків тренування
 *
 * Містить інформацію про блоки вправ та їх активність
 */
data class TrainingBlocksState(
    val blocks: PersistentList<TrainingBlockUiModel> = persistentListOf(),  // Блоки тренування
    val isWorkoutActive: Boolean = false,                                   // Чи активне тренування
    val currentBlockIndex: Int = 0                                          // Індекс поточного активного блоку
) {
    /**
     * Поточний активний блок тренування або null, якщо тренування неактивне
     */
    val currentBlock: TrainingBlockUiModel?
        get() = if (isWorkoutActive && blocks.isNotEmpty() && currentBlockIndex < blocks.size) {
            blocks[currentBlockIndex]
        } else {
            null
        }
}

/**
 * Стан діалогу вибору тренування
 *
 * Керує відображенням діалогу вибору програми та дня тренування
 */
data class SelectionState(
    val availablePrograms: PersistentList<ProgramInfo> = persistentListOf(),  // Доступні програми
    val selectedProgram: ProgramInfo? = null,                                 // Вибрана програма
    val availableGymDaySessions: PersistentMap<ProgramInfo, PersistentList<GymDayUiModel>> = persistentMapOf(),  // Дні по програмах
    val selectedGymDay: GymDayUiModel? = null,                                // Вибраний день
    val showSelectionDialog: Boolean = true,                                  // Чи показувати діалог вибору
    val isLoading: Boolean = false,                                           // Чи завантажуються дані
    val errorMessage: String? = null                                          // Повідомлення про помилку
) {
    /**
     * Доступні дні тренування для вибраної програми
     */
    val availableGymDaysForSelectedProgram: PersistentList<GymDayUiModel>
        get() = selectedProgram?.let { availableGymDaySessions[it] } ?: persistentListOf()

    /**
     * Чи готовий екран для початку тренування
     */
    val isReadyToStart: Boolean
        get() = selectedProgram != null && selectedGymDay != null
}

/**
 * Стан результатів тренування
 *
 * Зберігає результати усіх підходів під час тренування
 */
data class ResultsState(
    val workoutResults: PersistentMap<Long, PersistentList<ResultOfSet>> = persistentMapOf(),  // Результати, згруповані за часом
    val lastSavedTimestamp: Long? = null                                                        // Час останнього збереження
) {
    /**
     * Останній збережений результат або null, якщо результатів немає
     */
    val lastSavedResult: ResultOfSet?
        get() = lastSavedTimestamp?.let { timestamp ->
            workoutResults[timestamp]?.lastOrNull()
        }

    /**
     * Всі результати тренування у вигляді плоского списку
     */
    val allResults: List<ResultOfSet>
        get() = workoutResults.values.flatMap { it }
}