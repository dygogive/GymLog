package com.example.gymlog.presentation.workout

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.domain.usecase.GetGymDayWithResultsUseCase
import com.example.gymlog.presentation.FetchProgramsNewUiUseCase
import com.example.gymlog.presentation.mappers.toUiModel
import com.example.gymlog.ui.feature.workout.model.GymDayUiModel
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для вибору програми тренування та дня тренування.
 * Відповідає за завантаження доступних програм та керування вибором користувача.
 */
class ProgramSelectionViewModel @Inject constructor(
    private val fetchProgramsUseCase: FetchProgramsNewUiUseCase,
    private val getGymDayWithResultsUseCase: GetGymDayWithResultsUseCase,
    application: Application
) : AndroidViewModel(application) {

    // Стан вибору програми для UI
    private val _programSelectionState = MutableStateFlow(ProgramSelectionState())
    val programSelectionState = _programSelectionState.asStateFlow()

    /**
     * Ініціалізація ViewModel.
     * Виконується автоматично при створенні ViewModel.
     */
    init {
        loadPrograms()
    }

    /**
     * Завантажує всі доступні програми тренувань.
     * Встановлює стан завантаження (loading), обробляє помилки.
     */
    fun loadPrograms() {
        // Запускаємо корутину в області видимості ViewModel
        viewModelScope.launch {
            // Показуємо індикатор завантаження
            _programSelectionState.update { selectionState ->
                selectionState.copy(isLoading = true)
            }

            // Використовуємо runCatching для коректної обробки помилок
            runCatching {
                fetchProgramsUseCase(getApplication())
            }.onSuccess(fun(programs: List<ProgramInfo>) {
                // Зберігаємо програми і дні тренувань у стані
                _programSelectionState.update { selectionState ->
                    selectionState.copy(
                        availablePrograms = programs.toPersistentList(),
                        // Створюємо мапу програм до днів тренувань
                        availableGymDaySessions = programs
                            .associateWith(fun(it: ProgramInfo): PersistentList<GymDayUiModel> {
                                return it.gymDayUiModels.toPersistentList()
                            })
                            .toPersistentMap(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }).onFailure { error ->
                // Обробка помилок завантаження
                Log.e("ProgramSelectionVM", "Помилка завантаження програм", error)
                _programSelectionState.update { selectionState ->
                    selectionState.copy(
                        isLoading = false,
                        errorMessage = "Не вдалося завантажити програми: ${error.localizedMessage}"
                    )
                }
            }
        }
    }




    /**  оброблення тренування  **/

    /**
     * Обробляє вибір програми тренувань користувачем в UI.
     * @param program Вибрана програма тренувань
     */
    fun onProgramSelected(program: ProgramInfo) {
        _programSelectionState.update { selectionState ->
            selectionState.copy(
                selectedProgram = program,
                selectedGymDay = null // Скидаємо вибір дня при зміні програми
            )
        }
    }

    /**
     * Обробляє вибір дня тренування і закриває діалог вибору.
     * @param gymDay Вибраний день тренування
     * @param maxResultsPerExercise Максимальна кількість результатів для вправи
     * Завантажує тренування з результатами
     */
    fun onSessionSelected(gymDay: GymDayUiModel, maxResultsPerExercise: Int) {
        viewModelScope.launch {
            _programSelectionState.update { it.copy(
                selectedGymDay = gymDay,
                showSelectionDialog = false,
                isLoading = true
            )}

            val programUuid: String? = programSelectionState.value.selectedProgram?.uuid

            try {
                if(programUuid == null ) {
                    throw Exception("programUuid is null")
                }
                val gymDayWithResults = getGymDayWithResultsUseCase(
                    programUuid = programUuid,
                    gymDayId = gymDay.id,
                    maxResultsPerExercise = maxResultsPerExercise,
                )

                val uiModelGymDay = gymDayWithResults.toUiModel(getApplication())

                _programSelectionState.update { it.copy(
                    isLoading = false,
                    selectedGymDay = uiModelGymDay
                )}
            } catch (e: Exception) {
                _programSelectionState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Failed to load workout results"
                )}
            }
        }
    }

    /**
     * Закриває діалог вибору програми/дня тренування.
     * Викликається при натисканні на кнопку "Скасувати" або поза діалогом.
     */
    fun dismissSelectionDialog() {
        _programSelectionState.update { selectionState ->
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





    /**
     * Оновлює вибраний день тренування з новими даними
     */
    fun updateSelectedGymDay(gymDayUiModel: GymDayUiModel) {
        _programSelectionState.update { it.copy(
            selectedGymDay = gymDayUiModel
        )}
    }



    /**
     * Оновлює стан завантаження.
     * @param isLoading Чи відбувається завантаження
     */
    fun updateLoadingState(isLoading: Boolean) {
        _programSelectionState.update { it.copy(isLoading = isLoading) }
    }
}