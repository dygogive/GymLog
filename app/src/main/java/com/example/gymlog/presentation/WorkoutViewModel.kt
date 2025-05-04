package com.example.gymlog.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.usecase.GetWorkoutResultsUseCase
import com.example.gymlog.domain.usecase.SaveWorkoutResultUseCase
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
import java.util.Date
import java.util.Locale
import javax.inject.Inject


/**
 * ViewModel для екрану тренувань.
 * Відповідає за логіку екрану тренувань, керування програмами, днями тренувань,
 * таймером та збереження результатів.
 */
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val fetchProgramsUseCase: FetchProgramsNewUiUseCase,
    private val saveResultUseCase: SaveWorkoutResultUseCase,
    private val getResultsUseCase: GetWorkoutResultsUseCase,
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
        // Запам'ятовуємо вибір користувача
        updateSelectionState { selectionState ->
            selectionState.copy(
                selectedGymDay = session,
                showSelectionDialog = false
            )
        }

        // Показуємо список тренувальних блоків і активуємо статус тренування
        updateTrainingState { trainingState ->
            trainingState.copy(
                blocks = session.trainingBlocksUiModel.toPersistentList(),
                isTrainingBlockChosen = true
            )
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
     * @param weight Вага снаряду (не може бути null)
     * @param iteration Кількість повторень (може бути null)
     * @param workTime Час виконання в секундах (може бути null)
     * @param date Дата тренування
     * @param time Час тренування
     */
    fun saveResult(weight: Int?, iteration: Int?, workTime: Int?, date: String, time: String) {
        // Перевірка наявності хоча б одного значення
        if (weight == null && iteration == null && workTime == null) {
            Log.w("WorkoutVM", "Порожній результат")
            return
        }

        viewModelScope.launch {
            try {
                // Створюємо об'єкт результату підходу
                val result = ResultOfSet(
                    weight = weight,
                    iteration = iteration,
                    workTime = workTime,
                    currentDate = date,
                    currentTime = time
                )









                Log.d("WorkoutVM", "Результат збережено: $result")
            } catch (exception: Exception) {
                Log.e("WorkoutVM", "Помилка збереження", exception)
            }
        }
    }


    // ========================================================================================
    // ==== РЕЗУЛЬТАТИ ВИКОНАННЯ ВПРАВ =======================================================
    // ========================================================================================

    /**
     * Зберігає дату та час поточного тренування для відстеження історії.
     */
    private var currentWorkoutDateTime: String? = null

    /**
     * Зберігає результат виконання вправи в базу даних.
     * @param exerciseInBlockId ID вправи у блоці тренування
     * @param weight Вага снаряду (може бути null)
     * @param iteration Кількість повторень (може бути null)
     * @param workTime Час виконання в секундах (може бути null)
     * @param position Позиція підходу в серії
     * @param sequenceInGymDay Послідовність у дні тренування
     * @param timeFromStart Час від початку тренування в секундах
     */
    fun saveWorkoutResult(
        exerciseInBlockId: Long,
        weight: Int?,
        iteration: Int?,
        workTime: Int?,
        position: Int,
        sequenceInGymDay: Int,
        timeFromStart: Int
    ) {
        viewModelScope.launch {
            // Створюємо об'єкт з результатом вправи
            val result = WorkoutResult(
                exerciseInBlockId = exerciseInBlockId,
                weight = weight,
                iteration = iteration,
                workTime = workTime,
                position = position,
                sequenceInGymDay = sequenceInGymDay,
                timeFromStart = timeFromStart,
                // Використовуємо поточну дату/час тренування або створюємо нову
                workoutDateTime = currentWorkoutDateTime ?: getCurrentDateTime()
            )

            // Зберігаємо результат через use case
            saveResultUseCase(result)
        }
    }

    /**
     * Отримує історію результатів вправи.
     * Повертає два списки: поточні результати і історичні.
     *
     * @param exerciseInBlockId ID вправи у блоці тренування
     * @return Пара списків результатів: поточне тренування та історичні результати
     */
    suspend fun getWorkoutResults(exerciseInBlockId: Long): Pair<List<WorkoutResult>, List<WorkoutResult>> {
        return getResultsUseCase(exerciseInBlockId, currentWorkoutDateTime)
    }

    /**
     * Генерує поточну дату і час у форматі для збереження.
     * @return Строка дати та часу у форматі "yyyy-MM-dd HH:mm"
     */
    private fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    }


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
     * Викликається при знищенні ViewModel.
     * Важливо скасувати всі корутини для запобігання витоку пам'яті.
     */
    override fun onCleared() {
        super.onCleared()
        // Скасування таймера при знищенні ViewModel
        timerJob?.cancel()
    }
}