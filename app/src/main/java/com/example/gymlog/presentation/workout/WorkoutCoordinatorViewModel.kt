package com.example.gymlog.presentation.workout

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.core.utils.getCurrentDateTime
import com.example.gymlog.ui.feature.workout.model.GymDayUiModel
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Координаційний ViewModel, який об'єднує стани з інших ViewModels
 * для створення єдиного стану для всього екрану тренування.
 */
@HiltViewModel
class WorkoutCoordinatorViewModel @Inject constructor(
    private val timerViewModel: TimerViewModel,
    private val programSelectionViewModel: ProgramSelectionViewModel,
    private val trainingBlocksViewModel: TrainingBlocksViewModel,
    private val resultsViewModel: ResultsViewModel,
    application: Application
) : AndroidViewModel(application) {

    // Загальний стан для всього екрану тренування
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState = _uiState.asStateFlow()

    // Відстеження окремих станів для зручності
    val timerState: StateFlow<TimerState> = timerViewModel.timerState
    val programSelectionState: StateFlow<ProgramSelectionState> = programSelectionViewModel.programSelectionState
    val trainingBlocksState: StateFlow<TrainingBlocksState> = trainingBlocksViewModel.trainingBlocksState
    val gymDayState: StateFlow<GymDayState> = resultsViewModel.gymDayState

    // Дата і час тренування (запасний варіант якщо в timerState буде null)
    private var workoutDateTime: String = ""

    init {
        // Комбінуємо всі стани в один загальний стан
        viewModelScope.launch {
            combine(
                timerState,
                programSelectionState,
                trainingBlocksState,
                gymDayState,
            ) { timerState, programSelectionState, trainingBlocksState, gymDayState ->
                WorkoutUiState(
                    timerState = timerState,
                    programSelectionState = programSelectionState,
                    trainingBlocksState = trainingBlocksState,
                    gymDayState = gymDayState
                )
            }.collectLatest { combinedState ->
                _uiState.value = combinedState
            }
        }

        // Ініціалізуємо дату і час тренування при створенні ViewModel
        viewModelScope.launch {
            val datetime: String = getCurrentDateTime().first + " " + getCurrentDateTime().second
            workoutDateTime = datetime
            timerViewModel.setWorkoutDateTime(datetime)
            Log.d("datetime", "WorkoutCoordinatorViewModel.init: workoutDateTime = $workoutDateTime")
        }

        // Реагуємо на вибір нового дня тренування
        viewModelScope.launch {
            programSelectionState.collectLatest { selectionState ->
                selectionState.selectedGymDay?.let { gymDay ->
                    // Оновлюємо блоки тренування коли вибрано новий день
                    trainingBlocksViewModel.updateTrainingBlocks(gymDay.trainingBlocksUiModel)
                }
            }
        }
    }

    // ========================================================================================
    // ==== ДЕЛЕГОВАНІ МЕТОДИ ДО ОКРЕМИХ VIEWMODELS ==========================================
    // ========================================================================================

    /**
     * Запускає завантаження програм тренувань
     */
    fun loadPrograms() = programSelectionViewModel.loadPrograms()

    /**
     * Обробляє вибір програми тренувань
     */
    fun onProgramSelected(program: ProgramInfo) =
        programSelectionViewModel.onProgramSelected(program)

    /**
     * Обробляє вибір дня тренування
     */
    fun onSessionSelected(session: GymDayUiModel) {
        programSelectionViewModel.onSessionSelected(
            session,
            gymDayState.value.maxResultsPerExercise
        )
    }

    /**
     * Закриває діалог вибору
     */
    fun dismissSelectionDialog() = programSelectionViewModel.dismissSelectionDialog()

    /**
     * Повторює спробу завантаження програм
     */
    fun retryLoadPrograms() = programSelectionViewModel.retryLoadPrograms()

    /**
     * Запускає або зупиняє таймер тренування
     */
    fun startStopGym() = timerViewModel.startStopGym()

    /**
     * Фіксує завершення підходу
     */
    fun onSetFinish() = timerViewModel.onSetFinish()

    /**
     * Зберігає результат підходу
     */
    fun saveResult(
        exerciseInBlockId: Long,
        weight: Int?,
        iterations: Int?,
        workTime: Int?
    ) {
        val selectedGymDay = programSelectionState.value.selectedGymDay ?: return

        viewModelScope.launch {
            try {
                // Запускаємо індикатор завантаження
                programSelectionViewModel.updateLoadingState(true)

                // Отримуємо час від початку тренування
                val timeFromStart = timerViewModel.getCurrentWorkoutTimeMs()

                // Передаємо поточну дату і час тренування з timerViewModel або використовуємо запасний варіант
                val currentDateTime = timerState.value.dateTimeThisTraining ?: workoutDateTime

                Log.d("datetime", "WorkoutCoordinatorViewModel.saveResult: currentDateTime = $currentDateTime")

                // Зберігаємо результат
                val result = resultsViewModel.saveResult(
                    gymDayId = selectedGymDay.id,
                    exerciseInBlockId = exerciseInBlockId,
                    weight = weight,
                    iterations = iterations,
                    workTime = workTime,
                    timeFromStart = timeFromStart,
                    workoutDateTime = currentDateTime
                )

                // Обробляємо результат
                result.fold(
                    onSuccess = { updatedGymDay ->
                        // Оновлюємо програму і блоки
                        programSelectionViewModel.updateSelectedGymDay(updatedGymDay)
                        trainingBlocksViewModel.updateTrainingBlocks(updatedGymDay.trainingBlocksUiModel)
                    },
                    onFailure = { error ->
                        // Обробляємо помилку
                        Log.e("WorkoutCoordinator", "Error saving result", error)
                    }
                )
            } finally {
                // Прибираємо індикатор завантаження
                programSelectionViewModel.updateLoadingState(false)
            }
        }
    }

    fun onClickExpandExercise(exerciseId: Long) {
        timerViewModel.onClickExpandExercise(exerciseId)
    }
}