package com.example.gymlog.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.ui.feature.workout.model.TrainingBlockUiModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для керування блоками тренування.
 * Відповідає за відображення та взаємодію з блоками вправ.
 */
class TrainingBlocksViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    // Стан блоків тренування для UI
    private val _trainingBlocksState = MutableStateFlow(TrainingBlocksState())
    val trainingBlocksState = _trainingBlocksState.asStateFlow()

    /**
     * Оновлює блоки тренування новими даними.
     * @param blocks Нові блоки тренування
     */
    fun updateTrainingBlocks(blocks: List<TrainingBlockUiModel>) {
        viewModelScope.launch {
            _trainingBlocksState.update { currentState ->
                currentState.copy(
                    blocks = blocks.toPersistentList(),
                    isGymDayChosen = true
                )
            }
        }
    }

    /**
     * Встановлює статус вибраного дня тренування.
     * @param isChosen Чи вибрано день тренування
     */
    fun setGymDayChosen(isChosen: Boolean) {
        _trainingBlocksState.update { currentState ->
            currentState.copy(isGymDayChosen = isChosen)
        }
    }

    /**
     * Очищає блоки тренування.
     * Використовується коли користувач переключається на новий день тренування.
     */
    fun clearTrainingBlocks() {
        _trainingBlocksState.update { currentState ->
            currentState.copy(
                blocks = kotlinx.collections.immutable.persistentListOf(),
                isGymDayChosen = false
            )
        }
    }
}