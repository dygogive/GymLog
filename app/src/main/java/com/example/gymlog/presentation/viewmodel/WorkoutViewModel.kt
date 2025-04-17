package com.example.gymlog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repository.TrainingBlockRepository
import com.example.gymlog.data.repository.WorkoutRepository
import com.example.gymlog.presentation.screens_compose.workout.WorkoutUiState
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
 * ViewModel for Workout screen. Manages timers and loads training blocks.
 */
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val trainingBlockRepository: TrainingBlockRepository,
    private val workoutRepository: WorkoutRepository
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
        if (_uiState.value.isGymRunning) stopGym()
        else startGym()
    }

    private fun startGym() {
        val now = System.currentTimeMillis()
        startWorkoutTime = now
        startSetTime = now
        _uiState.update { it.copy(isGymRunning = true, totalTimeMs = 0L, lastSetTimeMs = 0L) }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isGymRunning) {
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
            val blocks = trainingBlockRepository
                .getTrainingBlocksByDayId(gymDayId)
                .toPersistentList()
            _uiState.update { it.copy(blocks = blocks) }
        }
    }
}
