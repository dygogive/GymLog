// WorkoutViewModel.kt
package com.example.gymlog.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.presentation.state.*
import com.example.gymlog.presentation.usecase.FetchProgramsUiUseCase
import com.example.gymlog.ui.feature.workout.model.GymDayUiModel
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для екрану тренувань
 *
 * Відповідає за логіку роботи з UI, керування таймером та стану тренувань.
 * Використовує FetchProgramsUiUseCase для отримання даних про програми тренувань.
 */
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val fetchProgramsUiUseCase: FetchProgramsUiUseCase,
    application: Application
) : AndroidViewModel(application) {

    // region Основні властивості

    private val context = application.applicationContext

    // Основний стан UI екрану
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    // Властивості для керування таймером
    private var timerJob: Job? = null
    private var startWorkoutTime = 0L      // Час початку всього тренування
    private var startSetTime = 0L          // Час початку поточного підходу

    // endregion

    // Ініціалізація ViewModel - завантаження програм
    init {
        loadPrograms()
    }

    // region Завантаження даних

    /**
     * Завантаження програм тренувань з репозиторію
     *
     * Викликає UseCase для отримання програм і оновлює стан UI
     * з новими даними. Обробляє помилки і логує їх.
     */
    private fun loadPrograms() {
        viewModelScope.launch {
            // Встановлюємо стан завантаження
            updateSelectionState { it.copy(isLoading = true) }

            runCatching { fetchProgramsUiUseCase(context) }
                .onSuccess { programsUi ->
                    // Оновлюємо список доступних програм
                    updateSelectionState {
                        it.copy(
                            availablePrograms = programsUi.toPersistentList(),
                            // Створюємо мапу програм до відповідних днів тренувань
                            availableGymDaySessions = programsUi
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
                            errorMessage = "Не вдалося завантажити програми тренувань: ${error.localizedMessage}"
                        )
                    }
                }
        }
    }

    // endregion

    // region Взаємодія з UI

    /**
     * Обробка вибору програми тренувань
     *
     * @param program Вибрана програма тренувань
     */
    fun onProgramSelected(program: ProgramInfo) = updateSelectionState {
        it.copy(selectedProgram = program, selectedGymDay = null)
    }

    /**
     * Обробка вибору конкретного дня тренування
     *
     * @param session Обраний день тренування
     */
    fun onSessionSelected(session: GymDayUiModel) {
        // Оновлюємо стан вибору
        updateSelectionState {
            it.copy(
                selectedGymDay = session,
                showSelectionDialog = false
            )
        }

        // Оновлюємо стан тренувальних блоків
        updateTrainingState {
            it.copy(
                blocks = session.trainingBlockUiModels.toPersistentList(),
                isWorkoutActive = true
            )
        }
    }

    /**
     * Перемикання стану тренування (старт/стоп)
     */
    fun startStopGym() {
        if (uiState.value.timerState.isGymRunning) {
            stopGym()
        } else {
            startGym()
        }
    }

    /**
     * Повідомлення про завершення підходу
     * Скидає лічильник часу для поточного підходу
     */
    fun onSetFinish() {
        startSetTime = System.currentTimeMillis()
        updateTimerState { it.copy(lastSetTimeMs = 0L) }
    }

    /**
     * Закриття діалогу вибору тренування
     */
    fun dismissSelectionDialog() {
        updateSelectionState { it.copy(showSelectionDialog = false) }
    }

    /**
     * Повторне завантаження програм (наприклад, після помилки)
     */
    fun retryLoadPrograms() {
        loadPrograms()
    }

    // endregion

    // region Керування таймером

    /**
     * Запуск таймера тренування
     *
     * Ініціює відлік загального часу та часу підходу
     */
    private fun startGym() {
        // Зберігаємо час запуску
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

        // Запускаємо корутину для оновлення таймера
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
     * Зупинка таймера тренування
     */
    private fun stopGym() {
        timerJob?.cancel()
        updateTimerState { it.copy(isGymRunning = false) }
    }

    // endregion

    // region Збереження результатів

    /**
     * Збереження результату тренування
     *
     * @param weight Вага, яку використовував користувач (кг)
     * @param iteration Кількість повторень
     * @param workTime Тривалість підходу (сек)
     * @param date Дата тренування
     * @param time Час тренування
     */
    fun saveResult(weight: Int?, iteration: Int?, workTime: Int?, date: String, time: String) {
        // Валідація даних перед збереженням
        if (weight == null && iteration == null && workTime == null) {
            Log.w("WorkoutVM", "Спроба збереження порожнього результату")
            return
        }

        viewModelScope.launch {
            try {
                // Створюємо об'єкт результату
                val result = ResultOfSet(
                    weight = weight,
                    iteration = iteration,
                    workTime = workTime,
                    currentDate = date,
                    currentTime = time
                )

                // Тут можна додати код для збереження результату в базу даних
                // ...

                // Оновлюємо UI зі збереженими результатами
                val timestamp = System.currentTimeMillis()
                updateResultsState { state ->
                    val currentResults = state.workoutResults[timestamp] ?: persistentListOf()
                    val updatedResults = (currentResults + result).toPersistentList()

                    state.copy(
                        workoutResults = state.workoutResults.put(timestamp, updatedResults)
                    )
                }

                Log.d("WorkoutVM", "Результат успішно збережено: $result")
            } catch (e: Exception) {
                Log.e("WorkoutVM", "Помилка збереження результату", e)
            }
        }
    }

    // endregion

    // region Допоміжні функції оновлення стану

    /**
     * Оновлення стану таймера
     */
    private fun updateTimerState(update: (TimerState) -> TimerState) {
        _uiState.update { it.copy(timerState = update(it.timerState)) }
    }

    /**
     * Оновлення стану блоків тренування
     */
    private fun updateTrainingState(update: (TrainingBlocksState) -> TrainingBlocksState) {
        _uiState.update { it.copy(trainingBlocksState = update(it.trainingBlocksState)) }
    }

    /**
     * Оновлення стану вибору програм/днів
     */
    private fun updateSelectionState(update: (SelectionState) -> SelectionState) {
        _uiState.update { it.copy(selectionState = update(it.selectionState)) }
    }

    /**
     * Оновлення стану результатів
     */
    private fun updateResultsState(update: (ResultsState) -> ResultsState) {
        _uiState.update { it.copy(resultsState = update(it.resultsState)) }
    }

    // endregion

    /**
     * Очищення ресурсів при знищенні ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}