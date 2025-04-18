package com.example.gymlog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repository.plan.TrainingBlockRepository
import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import com.example.gymlog.domain.usecase.GetTrainingBlocksByDayIdUseCase
import com.example.gymlog.domain.usecase.workout.GetAllGymDaysUseCase
import com.example.gymlog.domain.usecase.workout.InsertGymDayUseCase
import com.example.gymlog.presentation.state.WorkoutUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Workout screen. Manages timers, training blocks and workout days.
 */
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val getTrainingBlocksByDayIdUseCase: GetTrainingBlocksByDayIdUseCase,
    private val getAllGymDaysUseCase: GetAllGymDaysUseCase,
    private val insertGymDayUseCase: InsertGymDayUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startWorkoutTime = 0L
    private var startSetTime = 0L

    /**
     * Toggles workout timer: starts if stopped, stops if running.
     */
    fun startStopGym() {
        if (uiState.value.isGymRunning) stopGym()
        else startGym()
    }

    private fun startGym() {
        val now = System.currentTimeMillis()
        startWorkoutTime = now
        startSetTime = now
        _uiState.update { it.copy(isGymRunning = true, totalTimeMs = 0L, lastSetTimeMs = 0L) }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (uiState.value.isGymRunning) {
                val current = System.currentTimeMillis()
                _uiState.update { state ->
                    state.copy(
                        totalTimeMs = current - startWorkoutTime,
                        lastSetTimeMs = current - startSetTime
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

    /**
     * Call when a set (approach) is finished: resets rest timer.
     */
    fun onSetFinish() {
        startSetTime = System.currentTimeMillis()
        _uiState.update { it.copy(lastSetTimeMs = 0L) }
    }

    /**
     * Loads training blocks once for given gymDayId.
     */
    fun loadTrainingBlocksOnce(gymDayId: Long) {
        viewModelScope.launch {
            val blocks = getTrainingBlocksByDayIdUseCase(gymDayId)
                .toPersistentList()
            _uiState.update { it.copy(blocks = blocks) }
        }
    }

    /**
     * Loads all workout days via domain use case.
     * You can update UI state to hold days if needed.
     */
    fun loadAllGymDays() {
        viewModelScope.launch {
            getAllGymDaysUseCase()
                .collect { days ->
                    // TODO: handle `days` in UI state when introduced
                }
        }
    }

    /**
     * Inserts a new workout day via domain use case.
     */
    fun insertGymDay(day: WorkoutGymDayEntity) {
        viewModelScope.launch {
            insertGymDayUseCase(day)
        }
    }
}
