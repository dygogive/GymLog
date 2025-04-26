// WorkoutViewModel.kt
package com.example.gymlog.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.domain.usecase.GetTrainingBlocksByDayIdUseCase
import com.example.gymlog.domain.usecase.gym_day.GetGymSessionByProgramIdUseCase
import com.example.gymlog.domain.usecase.gym_plan.GetFitnessProgramsUseCase
import com.example.gymlog.presentation.state.ResultsState
import com.example.gymlog.presentation.state.SelectionState
import com.example.gymlog.presentation.state.TimerState
import com.example.gymlog.presentation.state.TrainingState
import com.example.gymlog.presentation.state.WorkoutUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val getTrainingBlocksByDayIdUseCase: GetTrainingBlocksByDayIdUseCase,
    private val getFitnessProgramsUseCase: GetFitnessProgramsUseCase,
    private val getGymSessionByProgramIdUseCase: GetGymSessionByProgramIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startWorkoutTime = 0L
    private var startSetTime = 0L

    init {
        loadPrograms()
    }

    // MARK: - Program Selection

    /**
     * Завантаження доступних програм тренувань та відповідних їм днів
     */
    private fun loadPrograms() {
        viewModelScope.launch {
            try {
                // Завантажуємо програми
                val programs = getFitnessProgramsUseCase()

                // Оновлюємо доступні програми в UI стані
                updateSelectionState { currentState ->
                    currentState.copy(availablePrograms = programs.toPersistentList())
                }

                // Завантажуємо дні тренувань для кожної програми
                val sessionsByProgram = programs
                    .associate { program ->
                        program.id to getGymSessionByProgramIdUseCase(program.id).toPersistentList()
                    }
                    .toPersistentMap()

                // Оновлюємо доступні дні тренувань в UI стані
                updateSelectionState { currentState ->
                    currentState.copy(availableGymDaySessions = sessionsByProgram)
                }
            } catch (e: Exception) {
                Log.e("WorkoutViewModel", "Error loading programs", e)
            }
        }
    }


    /**
     * Приховання діалогу вибору тренування
     */
    fun dismissSelectionDialog() {
        updateSelectionState { it.copy(showSelectionDialog = false) }
    }

    /**
     * Обробка вибору програми тренувань
     */
    fun onProgramSelected(program: FitnessProgram) {
        updateSelectionState {
            it.copy(
                selectedProgram = program,
                selectedGymDay = null
            )
        }
    }

    /**
     * Обробка вибору дня тренування
     */
    fun onSessionSelected(session: GymDay) {
        updateSelectionState {
            it.copy(
                selectedGymDay = session,
                showSelectionDialog = false
            )
        }
        loadTrainingBlocksForSession(session.id)
    }

    // MARK: - Training Blocks

    /**
     * Завантаження блоків тренування для вибраного дня
     */
    private fun loadTrainingBlocksForSession(gymDayId: Long) {
        viewModelScope.launch {
            try {
                val blocks = getTrainingBlocksByDayIdUseCase(gymDayId)
                    .toPersistentList()

                updateTrainingState { it.copy(
                    blocks = blocks,
                    isWorkoutActive = true
                )}
            } catch (e: Exception) {
                Log.e("WorkoutViewModel", "Error loading training blocks", e)
                // TODO: Додати обробку помилок і сповіщення користувача
            }
        }
    }

    // MARK: - Timer Management

    /**
     * Перемикання стану таймера (запуск/зупинка)
     */
    fun startStopGym() {
        if (uiState.value.timerState.isGymRunning) {
            stopGym()
        } else {
            startGym()
        }
    }

    /**
     * Запуск відліку часу тренування
     */
    private fun startGym() {
        // Записуємо час початку
        startWorkoutTime = System.currentTimeMillis()
        startSetTime = startWorkoutTime

        // Оновлюємо стан таймера
        updateTimerState {
            it.copy(
                isGymRunning = true,
                totalTimeMs = 0L,
                lastSetTimeMs = 0L
            )
        }

        // Запускаємо корутину для оновлення таймера кожну секунду
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (uiState.value.timerState.isGymRunning) {
                val now = System.currentTimeMillis()
                updateTimerState { state ->
                    state.copy(
                        totalTimeMs = now - startWorkoutTime,
                        lastSetTimeMs = now - startSetTime
                    )
                }
                delay(1000L) // Оновлення кожну секунду
            }
        }
    }

    /**
     * Зупинка відліку часу тренування
     */
    private fun stopGym() {
        timerJob?.cancel()
        updateTimerState { it.copy(isGymRunning = false) }
    }

    /**
     * Фіксація завершення поточного підходу
     */
    fun onSetFinish() {
        startSetTime = System.currentTimeMillis()
        updateTimerState { it.copy(lastSetTimeMs = 0L) }
    }

    // MARK: - Results Handling

    /**
     * Збереження результату підходу вправи
     */
    fun saveResult(weight: Int?, iteration: Int?, workTime: Int?, currentDate: String, currentTime: String) {
        Log.d("WorkoutViewModel", "Saving result: w=$weight i=$iteration t=$workTime date=$currentDate time=$currentTime")

        // TODO: Реалізувати логіку збереження результатів
        // Приклад:
        // val exerciseId = ... // ID поточної вправи
        // val newResult = WorkoutResult(exerciseId, weight, iteration, workTime, currentDate, currentTime)
        // updateResultsState { state ->
        //    val currentResults = state.workoutResults[exerciseId] ?: persistentListOf()
        //    val updatedResults = (currentResults + newResult).toPersistentList()
        //    state.copy(
        //        workoutResults = state.workoutResults.put(exerciseId, updatedResults)
        //    )
        // }
    }

    // MARK: - Helper Methods for State Updates

    /**
     * Оновлення частини стану для таймера
     */
    private fun updateTimerState(update: (TimerState) -> TimerState) {
        _uiState.update { currentState ->
            currentState.copy(timerState = update(currentState.timerState))
        }
    }

    /**
     * Оновлення частини стану для тренувальних блоків
     */
    private fun updateTrainingState(update: (TrainingState) -> TrainingState) {
        _uiState.update { currentState ->
            currentState.copy(trainingState = update(currentState.trainingState))
        }
    }

    /**
     * Оновлення частини стану для вибору тренування
     */
    private fun updateSelectionState(update: (SelectionState) -> SelectionState) {
        _uiState.update { currentState ->
            currentState.copy(selectionState = update(currentState.selectionState))
        }
    }

    /**
     * Оновлення частини стану для результатів
     */
    private fun updateResultsState(update: (ResultsState) -> ResultsState) {
        _uiState.update { currentState ->
            currentState.copy(resultsState = update(currentState.resultsState))
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}