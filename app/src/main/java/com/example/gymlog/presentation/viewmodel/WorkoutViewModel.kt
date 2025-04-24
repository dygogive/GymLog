// WorkoutViewModel.kt
package com.example.gymlog.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.model.plan.FitnessProgram
import com.example.gymlog.domain.model.plan.GymDay
import com.example.gymlog.domain.usecase.GetTrainingBlocksByDayIdUseCase
import com.example.gymlog.domain.usecase.gym_day.GetGymSessionByProgramIdUseCase
import com.example.gymlog.domain.usecase.gym_plan.GetFitnessProgramsUseCase
import com.example.gymlog.presentation.state.WorkoutUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
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

    private fun loadPrograms() {
        viewModelScope.launch {
            val programs = getFitnessProgramsUseCase()
            _uiState.update { it.copy(
                availablePrograms = programs.toPersistentList()
            ) }

            val sessionsByProgram = programs
                .associate { prog ->
                    prog.id to getGymSessionByProgramIdUseCase(prog.id).toPersistentList()
                }
                .toPersistentMap()

            _uiState.update { it.copy(
                availableGymDaySessions = sessionsByProgram
            ) }
        }
    }

    fun dismissSelectionDialog() {
        _uiState.update { it.copy(showSelectionDialog = false) }
    }

    fun onProgramSelected(program: FitnessProgram) {
        _uiState.update { it.copy(
            selectedProgram = program,
            selectedGymDay = null
        ) }
    }

    fun onSessionSelected(session: GymDay) {
        _uiState.update { it.copy(
            selectedGymDay = session,
            showSelectionDialog = false
        ) }
        loadTrainingBlocksOnce(session.id)
    }

    private fun loadTrainingBlocksOnce(gymDayId: Long) {
        viewModelScope.launch {
            val blocks = getTrainingBlocksByDayIdUseCase(gymDayId)
                .toPersistentList()
            _uiState.update { it.copy(blocks = blocks) }
        }
    }

    fun startStopGym() {
        if (uiState.value.isGymRunning) stopGym() else startGym()
    }

    private fun startGym() {
        startWorkoutTime = System.currentTimeMillis()
        startSetTime = startWorkoutTime
        _uiState.update { it.copy(
            isGymRunning = true,
            totalTimeMs = 0L,
            lastSetTimeMs = 0L
        ) }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (uiState.value.isGymRunning) {
                val now = System.currentTimeMillis()
                _uiState.update { st ->
                    st.copy(
                        totalTimeMs = now - startWorkoutTime,
                        lastSetTimeMs = now - startSetTime
                    )
                }
                delay(1000L)
            }
        }
    }

    private fun stopGym() {
        timerJob?.cancel()
        _uiState.update { it.copy(isGymRunning = false) }
    }

    fun onSetFinish() {
        startSetTime = System.currentTimeMillis()
        _uiState.update { it.copy(lastSetTimeMs = 0L) }
    }

    /**
     * Зберігаємо результат підходу: додаємо новий WorkoutResult
     */
    fun saveResult(weight: Int?, iteration: Int?, workTime: Int?,currentDate: String,currentTime: String) {
        Log.d("log_view_model", "saveResult: $weight $iteration $workTime $currentDate $currentTime")
    }
}
