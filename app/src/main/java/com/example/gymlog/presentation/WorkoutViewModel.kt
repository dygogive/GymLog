package com.example.gymlog.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.ui.feature.workout.model.GymDayUiModel
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для екрану тренувань
 */
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val fetchProgramsUseCase: FetchProgramsNewUiUseCase,
    application: Application
) : AndroidViewModel(application) {

    // UI стан
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState = _uiState.asStateFlow()

    // Таймер
    private var timerJob: Job? = null
    private var startWorkoutTime = 0L  // час початку тренування
    private var startSetTime = 0L      // час початку підходу

    init {
        loadPrograms()
    }







    // ----- ЗАВАНТАЖЕННЯ ДАНИХ - УСІХ ПРОГРАМ ТРЕНУВАНЬ -----

    private fun loadPrograms() {
        //використання корутини
        viewModelScope.launch {
            // Показуємо лоадер
            updateSelectionState { it.copy(isLoading = true) }


            runCatching { fetchProgramsUseCase(getApplication()) }
                .onSuccess { programs ->
                    // Зберігаємо програми і дні тренувань
                    updateSelectionState {
                        it.copy(
                            availablePrograms = programs.toPersistentList(),
                            availableGymDaySessions = programs
                                .associateWith { program -> program.gymDayUiModels.toPersistentList() }
                                .toPersistentMap(),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    Log.e("WorkoutVM", "Помилка завантаження програм", error)
                    updateSelectionState {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Не вдалося завантажити програми: ${error.localizedMessage}"
                        )
                    }
                }
        }
    }






    // ----- ОБРОБНИКИ ПОДІЙ UI -----

    // Вибір програми в діалозі
    fun onProgramSelected(program: ProgramInfo) = updateSelectionState {
        it.copy(selectedProgram = program, selectedGymDay = null)
    }
    // Вибір дня тренування і закриття діалогу
    fun onSessionSelected(session: GymDayUiModel) {
        // Запам'ятовуємо вибір
        updateSelectionState {
            it.copy(
                selectedGymDay = session,
                showSelectionDialog = false
            )
        }

        // Показуємо список тренувальних блоків і робимо тренування активним статусом
        updateTrainingState {
            it.copy(
                blocks = session.trainingBlocksUiModel.toPersistentList(),
                isWorkoutActive = true
            )
        }
    }


    // Старт/стоп тренування
    fun startStopGym() {
        if (uiState.value.timerState.isGymRunning) {
            stopGym()
        } else {
            startGym()
        }
    }

    // Завершення підходу
    fun onSetFinish() {
        startSetTime = System.currentTimeMillis()
        updateTimerState { it.copy(lastSetTimeMs = 0L) }
    }

    // Закриття діалогу вибору
    fun dismissSelectionDialog() {
        updateSelectionState { it.copy(showSelectionDialog = false) }
    }

    // Повторне завантаження
    fun retryLoadPrograms() {
        loadPrograms()
    }











    // ----- РОБОТА З ТАЙМЕРОМ -----

    // Запуск таймера
    private fun startGym() {
        startWorkoutTime = System.currentTimeMillis()
        startSetTime = startWorkoutTime

        updateTimerState {
            it.copy(
                isGymRunning = true,
                totalTimeMs = 0L,
                lastSetTimeMs = 0L
            )
        }

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
                delay(1000L)
            }
        }
    }

    // Зупинка таймера
    private fun stopGym() {
        timerJob?.cancel()
        updateTimerState { it.copy(isGymRunning = false) }
    }








    // ----- РОБОТА З РЕЗУЛЬТАТАМИ -----

    // Збереження результату підходу
    fun saveResult(weight: Int?, iteration: Int?, workTime: Int?, date: String, time: String) {
        // Перевірка даних
        if (weight == null && iteration == null && workTime == null) {
            Log.w("WorkoutVM", "Порожній результат")
            return
        }

        viewModelScope.launch {
            try {
                // Створюємо результат
                val result = ResultOfSet(
                    weight = weight,
                    iteration = iteration,
                    workTime = workTime,
                    currentDate = date,
                    currentTime = time
                )

                // Зберігаємо результат
                val timestamp = System.currentTimeMillis()
                updateResultsState { state ->
                    val currentResults = state.workoutResults[timestamp] ?: persistentListOf()
                    val updatedResults = (currentResults + result).toPersistentList()

                    state.copy(
                        workoutResults = state.workoutResults.put(timestamp, updatedResults)
                    )
                }

                Log.d("WorkoutVM", "Результат збережено: $result")
            } catch (e: Exception) {
                Log.e("WorkoutVM", "Помилка збереження", e)
            }
        }
    }







    // ----- ДОПОМІЖНІ ФУНКЦІЇ ОНОВЛЕННЯ СТАНУ -----

    private fun updateTimerState(update: (TimerState) -> TimerState) {
        _uiState.update { it.copy(timerState = update(it.timerState)) }
    }

    private fun updateTrainingState(update: (TrainingBlocksState) -> TrainingBlocksState) {
        _uiState.update { it.copy(trainingBlocksState = update(it.trainingBlocksState)) }
    }

    private fun updateSelectionState(update: (ProgramSelectionState) -> ProgramSelectionState) {
        _uiState.update { it.copy(programSelectionState = update(it.programSelectionState)) }
    }

    private fun updateResultsState(update: (ResultsState) -> ResultsState) {
        _uiState.update { it.copy(resultsState = update(it.resultsState)) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}