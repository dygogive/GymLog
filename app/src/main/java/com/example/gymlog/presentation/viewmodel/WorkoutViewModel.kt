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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel використовує єдиний UI UseCase: FetchProgramsUiUseCase
 */
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val fetchProgramsUiUseCase: FetchProgramsUiUseCase,
    application: Application
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startWorkoutTime = 0L
    private var startSetTime = 0L

    init { loadPrograms() }

    private fun loadPrograms() {
        viewModelScope.launch {
            runCatching { fetchProgramsUiUseCase(context) }
                .onSuccess { programsUi ->
                    // update available programs
                    updateSelectionState { it.copy(availablePrograms = programsUi.toPersistentList()) }
                    // map each program to its nested gym days
                    val sessionsByProgram = programsUi.associateWith { it.gymDayUiModels.toPersistentList() }
                        .toPersistentMap()
                    updateSelectionState { it.copy(availableGymDaySessions = sessionsByProgram) }
                }
                .onFailure { Log.e("WorkoutVM", "loadPrograms", it) }
        }
    }

    fun onProgramSelected(program: ProgramInfo) = updateSelectionState {
        it.copy(selectedProgram = program, selectedGymDay = null)
    }

    fun onSessionSelected(session: GymDayUiModel) {
        updateSelectionState { it.copy(selectedGymDay = session, showSelectionDialog = false) }
        // UI model already contains training blocks
        updateTrainingState { it.copy(blocks = session.trainingBlockUiModels.toPersistentList(), isWorkoutActive = true) }
    }

    fun startStopGym() { if (uiState.value.timerState.isGymRunning) stopGym() else startGym() }

    private fun startGym() {
        startWorkoutTime = System.currentTimeMillis(); startSetTime = startWorkoutTime
        updateTimerState { it.copy(isGymRunning = true, totalTimeMs = 0L, lastSetTimeMs = 0L) }
        timerJob?.cancel(); timerJob = viewModelScope.launch {
            while (uiState.value.timerState.isGymRunning) {
                val now = System.currentTimeMillis()
                updateTimerState { s -> s.copy(totalTimeMs = now - startWorkoutTime, lastSetTimeMs = now - startSetTime) }
                delay(1000L)
            }
        }
    }

    private fun stopGym() {
        timerJob?.cancel()
        updateTimerState { it.copy(isGymRunning = false) }
    }

    fun onSetFinish() {
        startSetTime = System.currentTimeMillis()
        updateTimerState { it.copy(lastSetTimeMs = 0L) }
    }

    fun saveResult(weight: Int?, iteration: Int?, workTime: Int?, date: String, time: String) {
        // ...
    }

    private fun updateTimerState(u: (TimerState) -> TimerState) = _uiState.update { it.copy(timerState = u(it.timerState)) }
    private fun updateTrainingState(u: (TrainingState) -> TrainingState) = _uiState.update { it.copy(trainingState = u(it.trainingState)) }
    private fun updateSelectionState(u: (SelectionState) -> SelectionState) = _uiState.update { it.copy(selectionState = u(it.selectionState)) }
    private fun updateResultsState(u: (ResultsState) -> ResultsState) = _uiState.update { it.copy(resultsState = u(it.resultsState)) }

    override fun onCleared() { super.onCleared(); timerJob?.cancel() }



    // додай у WorkoutViewModel:

    fun dismissSelectionDialog() {
        updateSelectionState { it.copy(showSelectionDialog = false) }
    }
}
