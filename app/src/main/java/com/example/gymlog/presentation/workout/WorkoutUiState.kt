package com.example.gymlog.presentation.workout

import com.example.gymlog.core.utils.getCurrentDateTime
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
    val gymDayState: GymDayState = GymDayState(),           // Стан вибору програми

)



/**
 * Стан вибору програми тренування
 */
data class ProgramSelectionState(
    val availablePrograms: PersistentList<ProgramInfo> = persistentListOf(),  // Доступні програми
    val selectedProgram: ProgramInfo? = null,  // Вибрана програма

    // Дні тренувань по програмах
    val availableGymDaySessions: PersistentMap<ProgramInfo, PersistentList<GymDayUiModel>> = persistentMapOf(),

    val selectedGymDay: GymDayUiModel? = null,  // Вибраний день з блоками, вправами і результатами всередині
    val showSelectionDialog: Boolean = true,    // Чи показувати діалог вибору
    val isLoading: Boolean = false,             // Іде завантаження
    val errorMessage: String? = null            // Помилка
)



/**
 * Стан блоків тренування
 */
data class TrainingBlocksState(
    val blocks: PersistentList<TrainingBlockUiModel> = persistentListOf(),  // Блоки вправ
    val isGymDayChosen: Boolean = false,  // Чи вибрано тренувальний блок щоб закрити діалог і відкрити вікно тренування?
)

/**
 * Стан таймера
 */
data class TimerState(
    val totalTimeMs: Long = 0L,        // Загальний час
    val lastSetTimeMs: Long = 0L,      // Час підходу
    val isGymRunning: Boolean = false,  // Чи активне тренування
    val expandedExerciseId: Long = -1,
    val dateTimeThisTraining: String? = null,    //Унікальний ідентифікатор тренування
)


data class GymDayState(
    val resultsAdded: Int = 0,
    val errorSaveResult: String = "error",
    val maxResultsPerExercise: Int = 5,
    val isEditDialogVisible: Boolean = false,
)

