// 📁 ui/screens/workout/WorkoutViewModel.kt
package com.example.gymlog.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repo: WorkoutRepository
) : ViewModel() {

    /* ---------------- State ---------------- */

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    /* ---------------- Таймери ---------------- */

    private var timerJob: Job? = null
    private var startTs = 0L
    private var restStartTs = 0L

    fun startTimers() {
        startTs = System.currentTimeMillis()
        restStartTs = startTs
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                _uiState.update {
                    it.copy(
                        totalTimeMs = now - startTs,
                        restTimeMs = now - restStartTs
                    )
                }
                kotlinx.coroutines.delay(1.seconds)
            }
        }
    }

    fun stopTimers() {
        timerJob?.cancel()
    }

    fun resetRestTimer() {
        restStartTs = System.currentTimeMillis()
    }

    /* ---------------- Завантаження даних ---------------- */

    fun observeSetsForDay(dayId: Long) {
        viewModelScope.launch {
            repo.getSetsForDay(dayId)
                .collect { list ->
                    _uiState.update { it.copy(sets = list.toPersistentList()) }
                }
        }
    }
}
