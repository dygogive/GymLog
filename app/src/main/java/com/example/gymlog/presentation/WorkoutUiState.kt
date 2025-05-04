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
    val programSelectionState: ProgramSelectionState = ProgramSelectionState(),           // Стан вибору програми
)



/**
 * Стан вибору програми тренування
 */
data class ProgramSelectionState(
    val availablePrograms: PersistentList<ProgramInfo> = persistentListOf(),  // Доступні програми
    val selectedProgram: ProgramInfo? = null,  // Вибрана програма

    // Дні тренувань по програмах
    val availableGymDaySessions: PersistentMap<ProgramInfo, PersistentList<GymDayUiModel>> = persistentMapOf(),

    val selectedGymDay: GymDayUiModel? = null,  // Вибраний день
    val showSelectionDialog: Boolean = true,    // Чи показувати діалог вибору
    val isLoading: Boolean = false,             // Іде завантаження
    val errorMessage: String? = null            // Помилка
)



/**
 * Стан блоків тренування
 */
data class TrainingBlocksState(
    val blocks: PersistentList<TrainingBlockUiModel> = persistentListOf(),  // Блоки вправ
    val isTrainingBlockChosen: Boolean = false,  // Чи вибрано тренувальний блок щоб закрити діалог і відкрити вікно тренування?
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




