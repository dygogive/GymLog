package com.example.gymlog.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.core.utils.getCurrentDateTime
import com.example.gymlog.domain.usecase.SaveResultUseCase
import com.example.gymlog.presentation.mappers.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel для керування результатами тренування.
 * Відповідає за збереження результатів підходів.
 */
class ResultsViewModel @Inject constructor(
    private val saveResultUseCase: SaveResultUseCase,
    application: Application
) : AndroidViewModel(application) {

    // Стан дня тренування для UI
    private val _gymDayState = MutableStateFlow(GymDayState())
    val gymDayState = _gymDayState.asStateFlow()

    /**
     * Зберігає результат підходу.
     * @param gymDayId ID дня тренування
     * @param exerciseInBlockId ID вправи в блоці
     * @param weight Вага
     * @param iterations Кількість повторень
     * @param workTime Час виконання
     * @param timeFromStart Час від початку тренування
     * @return Оновлений день тренування з новим результатом
     */
    suspend fun saveResult(
        gymDayId: Long,
        exerciseInBlockId: Long,
        weight: Int?,
        iterations: Int?,
        workTime: Int?,
        timeFromStart: Long
    ): Result<com.example.gymlog.ui.feature.workout.model.GymDayUiModel> {
        return try {
            // Отримуємо поточну дату й час
            val currentDateTime = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val currentDate = dateFormat.format(currentDateTime.time)
            val currentTime = timeFormat.format(currentDateTime.time)

            // Визначаємо порядковий номер результату в дні тренування
            val sequenceInGymDay = _gymDayState.value.resultsAdded

            // Зберігаємо результат
            val updatedGymDay = saveResultUseCase(
                gymDayId = gymDayId,
                maxResultsPerExercise = _gymDayState.value.maxResultsPerExercise,
                exerciseInBlockId = exerciseInBlockId,
                weight = weight,
                iterations = iterations,
                workTime = workTime,
                sequenceInGymDay = sequenceInGymDay,
                timeFromStart = timeFromStart,
                workoutDateTime = "$currentDate $currentTime",
            )

            // Оновлюємо лічильник доданих результатів
            incrementResultsAdded()

            // Повертаємо успішний результат з оновленим днем тренування
            Result.success(updatedGymDay.toUiModel(getApplication()))
        } catch (e: Exception) {
            // Повертаємо помилку
            Result.failure(e)
        }
    }

    /**
     * Збільшує лічильник доданих результатів.
     */
    private fun incrementResultsAdded() {
        _gymDayState.update { currentState ->
            currentState.copy(resultsAdded = currentState.resultsAdded + 1)
        }
    }

    /**
     * Встановлює максимальну кількість результатів для вправи.
     * @param maxResults Максимальна кількість результатів
     */
    fun setMaxResultsPerExercise(maxResults: Int) {
        _gymDayState.update { currentState ->
            currentState.copy(maxResultsPerExercise = maxResults)
        }
    }

    /**
     * Скидає кількість доданих результатів.
     * Використовується при зміні дня тренування.
     */
    fun resetResultsAdded() {
        _gymDayState.update { currentState ->
            currentState.copy(resultsAdded = 0)
        }
    }
}