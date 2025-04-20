package com.example.gymlog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.domain.model.plan.FitnessProgram
import com.example.gymlog.domain.model.plan.GymDay
import com.example.gymlog.domain.usecase.GetTrainingBlocksByDayIdUseCase
import com.example.gymlog.domain.usecase.gym_day.GetGymSessionByProgramIdUseCase
import com.example.gymlog.domain.usecase.gym_plan.GetFitnessProgramsUseCase
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

/**
 * ViewModel for Workout screen. Manages timers, training blocks and workout days.
 */
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
            _uiState.update { currentState ->
                currentState.copy(availablePrograms = programs.toPersistentList())
            }

            // Завантажуємо тренування для кожної програми
            val gymDayByProgram = mutableMapOf<Long, List<GymDay>>()
            programs.forEach { program ->
                val gymSessions = getGymSessionByProgramIdUseCase(program.id)
                gymDayByProgram[program.id] = gymSessions
            }

            _uiState.update { currentState ->
                currentState.copy(availableGymDaySessions = gymDayByProgram.toPersistentMap() )
            }
        }
    }


    // закрити діалог
    fun dismissSelectionDialog() {
        _uiState.update { it.copy(showSelectionDialog = false) }
    }






    // Виклик, коли користувач обрав програму
    fun onProgramSelected(program: FitnessProgram) {
        _uiState.update { it.copy(
            selectedProgram = program,
            // початково не обрана жодна сесія
            selectedGymDay = null
        )}
    }



    // Виклик, коли користувач обрав сесію
    fun onSessionSelected(session: GymDay) {
        _uiState.update { it.copy(
            selectedGymDay = session,
            showSelectionDialog = false  // більше не потрібен діалог
        )}
        // відразу підвантажуємо блоки для цього дня
        loadTrainingBlocksOnce(session.id)
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




    //function for fixing results of set in gym
    fun onSetWeightRepeats() {

    }

}
