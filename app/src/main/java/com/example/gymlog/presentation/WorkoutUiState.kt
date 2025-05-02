package com.example.gymlog.presentation

import com.example.gymlog.core.utils.formatTime
import com.example.gymlog.ui.feature.workout.model.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * Загальний стан екрану тренування
 */
data class WorkoutUiState(
    val timerState: TimerState = TimerState(),                       // Стан таймера
    val trainingBlocksState: TrainingBlocksState = TrainingBlocksState(),  // Стан блоків вправ
    val selectionState: SelectionState = SelectionState(),           // Стан вибору програми
    val resultsState: ResultsState = ResultsState()                  // Стан результатів
)

/**
 * Стан таймера
 */
data class TimerState(
    val totalTimeMs: Long = 0L,        // Загальний час
    val lastSetTimeMs: Long = 0L,      // Час підходу
    val isGymRunning: Boolean = false  // Чи активне тренування
) {
    // Форматований загальний час "гг:хх:сс"
    val formattedTotalTime: String
        get() = formatTime(totalTimeMs)

    // Форматований час підходу "хх:сс"
    val formattedSetTime: String
        get() = formatTime(lastSetTimeMs, includeHours = false)
}

/**
 * Стан блоків тренування
 */
data class TrainingBlocksState(
    val blocks: PersistentList<TrainingBlockUiModel> = persistentListOf(),  // Блоки вправ
    val isWorkoutActive: Boolean = false,  // Чи активне тренування
    val currentBlockIndex: Int = 0         // Індекс поточного блоку
) {
    // Поточний активний блок
    val currentBlock: TrainingBlockUiModel?
        get() = if (isWorkoutActive && blocks.isNotEmpty() && currentBlockIndex < blocks.size) {
            blocks[currentBlockIndex]
        } else {
            null
        }
}

/**
 * Стан вибору програми тренування
 */
data class SelectionState(
    val availablePrograms: PersistentList<ProgramInfo> = persistentListOf(),  // Доступні програми
    val selectedProgram: ProgramInfo? = null,  // Вибрана програма

    // Дні тренувань по програмах
    val availableGymDaySessions: PersistentMap<ProgramInfo, PersistentList<GymDayUiModel>> = persistentMapOf(),

    val selectedGymDay: GymDayUiModel? = null,  // Вибраний день
    val showSelectionDialog: Boolean = true,    // Чи показувати діалог вибору
    val isLoading: Boolean = false,             // Іде завантаження
    val errorMessage: String? = null            // Помилка
) {
    // Доступні дні для вибраної програми
    val availableGymDaysForSelectedProgram: PersistentList<GymDayUiModel>
        get() = selectedProgram?.let { availableGymDaySessions[it] } ?: persistentListOf()

    // Чи можна почати тренування
    val isReadyToStart: Boolean
        get() = selectedProgram != null && selectedGymDay != null
}

/**
 * Стан результатів тренування
 */
data class ResultsState(
    // Результати, згруповані за часом
    val workoutResults: PersistentMap<Long, PersistentList<ResultOfSet>> = persistentMapOf(),
    val lastSavedTimestamp: Long? = null  // Час останнього збереження
) {
    // Останній збережений результат
    val lastSavedResult: ResultOfSet?
        get() = lastSavedTimestamp?.let { timestamp ->
            workoutResults[timestamp]?.lastOrNull()
        }

    // Всі результати в одному списку
    val allResults: List<ResultOfSet>
        get() = workoutResults.values.flatMap { it }
}