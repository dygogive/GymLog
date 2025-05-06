package com.example.gymlog.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.core.utils.getCurrentDateTime
import com.example.gymlog.domain.usecase.GetGymDayWithResultsUseCase
import com.example.gymlog.domain.usecase.SaveResultUseCase
import com.example.gymlog.presentation.mappers.toUiModel
import com.example.gymlog.ui.feature.workout.model.GymDayUiModel
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.Int
import kotlin.String

/**
 * ViewModel для екрану тренувань.
 * Відповідає за логіку екрану тренувань, керування програмами, днями тренувань,
 * таймером та збереження результатів.
 */

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val fetchProgramsUseCase: FetchProgramsNewUiUseCase,
    private val getGymDayWithResultsUseCase: GetGymDayWithResultsUseCase,
    private val saveResultUseCase: SaveResultUseCase,
    application: Application
) : AndroidViewModel(application) {

    // UI стан для всього екрану - містить вкладені стани для різних частин екрану
    private val _uiState = MutableStateFlow(WorkoutUiState())

    // Публічний стан для UI, який доступний тільки для читання
    val uiState = _uiState.asStateFlow()

    // Змінні для відстеження часу тренування та часу підходу
    private var timerJob: Job? = null        // Job корутини для таймера
    private var startWorkoutTime = 0L        // час початку тренування в мілісекундах
    private var startSetTime = 0L            // час початку підходу в мілісекундах

    /**
     * Ініціалізація ViewModel.
     * Виконується автоматично при створенні ViewModel.
     */
    init {
        loadPrograms()
    }


    // ========================================================================================
    // ==== ЗАВАНТАЖЕННЯ ДАНИХ - УСІХ ПРОГРАМ ТРЕНУВАНЬ ======================================
    // ========================================================================================

    /**
     * Завантажує всі доступні програми тренувань.
     * Встановлює стан завантаження (loading), обробляє помилки.
     */
    private fun loadPrograms() {
        // Запускаємо корутину в області видимості ViewModel
        viewModelScope.launch {
            // Показуємо індикатор завантаження
            updateSelectionState { selectionState ->
                selectionState.copy(isLoading = true)
            }

            // Використовуємо runCatching для коректної обробки помилок
            runCatching {
                fetchProgramsUseCase(getApplication())
            }.onSuccess { programs ->
                // Зберігаємо програми і дні тренувань у стані
                updateSelectionState { selectionState ->
                    selectionState.copy(
                        availablePrograms = programs.toPersistentList(),
                        // Створюємо мапу програм до днів тренувань
                        availableGymDaySessions = programs
                            .associateWith {
                                it.gymDayUiModels.toPersistentList()
                            }
                            .toPersistentMap(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                // Обробка помилок завантаження
                Log.e("WorkoutVM", "Помилка завантаження програм", error)
                updateSelectionState { selectionState ->
                    selectionState.copy(
                        isLoading = false,
                        errorMessage = "Не вдалося завантажити програми: ${error.localizedMessage}"
                    )
                }
            }
        }
    }


    // ========================================================================================
    // ==== ОБРОБНИКИ ПОДІЙ UI ===============================================================
    // ========================================================================================

    /**
     * Обробляє вибір програми тренувань користувачем в UI.
     * @param program Вибрана програма тренувань
     */
    fun onProgramSelected(program: ProgramInfo) {
        updateSelectionState { selectionState ->
            selectionState.copy(
                selectedProgram = program,
                selectedGymDay = null // Скидаємо вибір дня при зміні програми
            )
        }
    }

    /**
     * Обробляє вибір дня тренування і закриває діалог вибору.
     * @param session Вибраний день тренування
     */
    fun onSessionSelected(session: GymDayUiModel) {
        viewModelScope.launch {
            updateSelectionState { it.copy(
                selectedGymDay = session,
                showSelectionDialog = false,
                isLoading = true
            )}

            try {
                val gymDayWithResults = getGymDayWithResultsUseCase(
                    gymDayId = session.id,
                    maxResultsPerExercise = uiState.value.gymDayState.maxResultsPerExercise,
                )

                val uiModelGymDay = gymDayWithResults.toUiModel(getApplication())

                updateSelectionState { it.copy(
                    isLoading = false,
                    selectedGymDay = uiModelGymDay
                )}

                updateTrainingState { it.copy(
                    blocks = uiModelGymDay.trainingBlocksUiModel.toPersistentList(),
                    isGymDayChosen = true
                )}
            } catch (e: Exception) {
                updateSelectionState { it.copy(
                    isLoading = false,
                    errorMessage = "Failed to load workout results"
                )}
            }
        }
    }







    /**
     * Запускає або зупиняє тренування в залежності від поточного стану.
     * Якщо тренування активне - зупиняє його, інакше - запускає.
     */
    fun startStopGym() {
        if (uiState.value.timerState.isGymRunning) {
            stopGym()
        } else {
            startGym()
        }
    }

    /**
     * Фіксує закінчення підходу і скидає таймер для поточного підходу.
     * Викликається коли користувач закінчив один підхід вправи.
     */
    fun onSetFinish() {
        // Запам'ятовуємо час початку нового підходу
        startSetTime = System.currentTimeMillis()
        // Скидаємо час підходу на 0
        updateTimerState { timerState ->
            timerState.copy(lastSetTimeMs = 0L)
        }
    }

    /**
     * Закриває діалог вибору програми/дня тренування.
     * Викликається при натисканні на кнопку "Скасувати" або поза діалогом.
     */
    fun dismissSelectionDialog() {
        updateSelectionState { selectionState ->
            selectionState.copy(showSelectionDialog = false)
        }
    }

    /**
     * Повторює спробу завантаження програм у випадку помилки.
     * Викликається коли користувач натискає на кнопку "Спробувати ще раз".
     */
    fun retryLoadPrograms() {
        loadPrograms()
    }


    // ========================================================================================
    // ==== РОБОТА З ТАЙМЕРОМ ================================================================
    // ========================================================================================

    /**
     * Запускає таймер тренування та підходу.
     * Оновлює поточний час тренування кожну секунду.
     */
    private fun startGym() {
        // Запам'ятовуємо час початку тренування і підходу
        startWorkoutTime = System.currentTimeMillis()
        startSetTime = startWorkoutTime

        // Оновлюємо стан таймера - починаємо відлік
        updateTimerState { timerState ->
            timerState.copy(
                isGymRunning = true,  // Позначаємо, що тренування активне
                totalTimeMs = 0L,     // Скидаємо загальний час тренування
                lastSetTimeMs = 0L    // Скидаємо час поточного підходу
            )
        }

        // Скасовуємо попередній таймер, якщо він був
        timerJob?.cancel()

        // Запускаємо нову корутину таймера
        timerJob = viewModelScope.launch {
            // Цикл оновлення часу, поки тренування активне
            while (uiState.value.timerState.isGymRunning) {
                val currentTimeMs = System.currentTimeMillis()

                // Оновлюємо час тренування і час підходу
                updateTimerState { timerState ->
                    timerState.copy(
                        totalTimeMs = currentTimeMs - startWorkoutTime,   // Загальний час тренування
                        lastSetTimeMs = currentTimeMs - startSetTime      // Час поточного підходу
                    )
                }

                // Затримка в 1 секунду перед наступним оновленням
                delay(1000L)
            }
        }
    }

    /**
     * Зупиняє таймер тренування.
     * Скасовує оновлення часу і змінює стан на "не активний".
     */
    private fun stopGym() {
        // Скасовує корутину таймера
        timerJob?.cancel()

        // Оновлює стан таймера на "зупинено"
        updateTimerState { timerState ->
            timerState.copy(isGymRunning = false)
        }
    }


    // ========================================================================================
    // ==== РОБОТА З РЕЗУЛЬТАТАМИ ПІДХОДІВ ===================================================
    // ========================================================================================

    /**
     * Зберігає результат підходу в локальний стан.
     */
    fun saveResult(
        exerciseInBlockId: Long,
        weight: Int?,
        iterations: Int?,
        workTime: Int?
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val selectedGymDay = currentState.programSelectionState.selectedGymDay ?: return@launch

            try {
                // Оновлюємо стан завантаження
                updateSelectionState { it.copy(isLoading = true) }

                //отримати поточну дату й час
                val currentDateTime = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val currentDate = dateFormat.format(currentDateTime.time)
                val currentTime = timeFormat.format(currentDateTime.time)

                val timeFromStart = System.currentTimeMillis() - currentState.timerState.totalTimeMs

                //вказати, що результатів додано на 1 більше
                updateGymDayState {it.copy(resultsAdded = it.resultsAdded + 1)}
                val sequenceInGymDay = currentState.gymDayState.resultsAdded


                //Зберігаємо результат
                val updatedGymDay = saveResultUseCase(
                    gymDayId = selectedGymDay.id,
                    maxResultsPerExercise = currentState.gymDayState.maxResultsPerExercise,
                    exerciseInBlockId = exerciseInBlockId,
                    weight = weight,
                    iterations = iterations,
                    workTime = workTime,
                    sequenceInGymDay = sequenceInGymDay,
                    timeFromStart = timeFromStart,
                    workoutDateTime = "$currentDate $currentTime",
                )




                // Оновлюємо лічильник результатів
                updateGymDayState { it.copy(resultsAdded = it.resultsAdded + 1) }

                // Оновлюємо стан
                updateSelectionState { it.copy(
                    selectedGymDay = updatedGymDay.toUiModel(getApplication()),
                    isLoading = false
                )}
                updateTrainingState { it.copy(
                    blocks = updatedGymDay.trainingBlocks.map { it.toUiModel(getApplication()) }.toPersistentList()
                )}


            } catch (e: Exception) {
                updateSelectionState { it.copy(
                    isLoading = false,
                    errorMessage = "Failed to save result"
                )}
            }
        }
    }


    // ========================================================================================
    // ==== РЕЗУЛЬТАТИ ВИКОНАННЯ ВПРАВ =======================================================
    // ========================================================================================


    // ========================================================================================
    // ==== ДОПОМІЖНІ ФУНКЦІЇ ОНОВЛЕННЯ СТАНУ ================================================
    // ========================================================================================

    /**
     * Оновлює стан таймера.
     * @param update функція, що отримує поточний стан і повертає оновлений
     */
    private fun updateTimerState(update: (TimerState) -> TimerState) {
        _uiState.update { currentState ->
            currentState.copy(timerState = update(currentState.timerState))
        }
    }

    /**
     * Оновлює стан тренувальних блоків.
     * @param update функція, що отримує поточний стан і повертає оновлений
     */
    private fun updateTrainingState(update: (TrainingBlocksState) -> TrainingBlocksState) {
        _uiState.update { currentState ->
            currentState.copy(trainingBlocksState = update(currentState.trainingBlocksState))
        }
    }

    /**
     * Оновлює стан вибору програми тренування.
     * @param update функція, що отримує поточний стан і повертає оновлений
     */
    private fun updateSelectionState(update: (ProgramSelectionState) -> ProgramSelectionState) {
        _uiState.update { currentState ->
            currentState.copy(programSelectionState = update(currentState.programSelectionState))
        }
    }

    /**
     * Оновлює стан вибору програми тренування.
     * @param update функція, що отримує поточний стан і повертає оновлений
     */
    private fun updateGymDayState(update: (GymDayState) -> GymDayState) =
        _uiState.update { currentState -> currentState.copy(gymDayState = update(currentState.gymDayState)) }

    /**
     * Викликається при знищенні ViewModel.
     * Важливо скасувати всі корутини для запобігання витоку пам'яті.
     */
    override fun onCleared() {
        super.onCleared()
        // Скасування таймера при знищенні ViewModel
        timerJob?.cancel()
    }
}