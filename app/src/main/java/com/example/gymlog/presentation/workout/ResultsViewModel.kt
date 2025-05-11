package com.example.gymlog.presentation.workout

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.gymlog.domain.usecase.DeleteResultUseCase
import com.example.gymlog.domain.usecase.SaveResultUseCase
import com.example.gymlog.presentation.mappers.toUiModel
import com.example.gymlog.ui.feature.workout.model.GymDayUiModel
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel для керування результатами тренування.
 * Відповідає за збереження результатів підходів.
 */
class ResultsViewModel @Inject constructor(
    private val saveResultUseCase: SaveResultUseCase,
    private val deleteResultUseCase: DeleteResultUseCase,
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
     * @param workoutDateTime Дата і час тренування
     * @return Оновлений день тренування з новим результатом
     */
    suspend fun saveResult(
        gymDayId: Long,
        exerciseInBlockId: Long,
        weight: Int?,
        iterations: Int?,
        workTime: Int?,
        timeFromStart: Long,
        workoutDateTime: String
    ): Result<GymDayUiModel> {
        return try {
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
                workoutDateTime = workoutDateTime
            )

            // Оновлюємо лічильник доданих результатів
            incrementResultsAdded()

            // Повертаємо успішний результат з оновленим днем тренування
            return Result.success(updatedGymDay.toUiModel(getApplication()))
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
     * Зменшити лічильник доданих результатів.
     */
    private fun decrementResultsAdded() {
        _gymDayState.update { currentState ->
            currentState.copy(resultsAdded = currentState.resultsAdded - 1)
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



    //здійснити видалення результату
    suspend fun onDeleteResult(
        resultOfSet: ResultOfSet,
        gymDayId: Long,
        workoutDateTime: String,
    ): Result<GymDayUiModel> {
        return try {
            Log.d("onDeleteResult", "onDeleteResult: 3")
            val updatedGymDay = deleteResultUseCase(
                idResult = resultOfSet.id,
                gymDayId = gymDayId,
                maxResultsPerExercise = gymDayState.value.maxResultsPerExercise,
                workoutDateTime = workoutDateTime
            )
            Log.d("onDeleteResult", "onDeleteResult: 4")

            val currentWorkoutDateTime = "${resultOfSet.currentDate} ${resultOfSet.currentTime}"
            if (currentWorkoutDateTime == workoutDateTime) {
                decrementResultsAdded()
            }

            Log.d("onDeleteResult1", _gymDayState.value.resultsAdded.toString())
            Log.d("onDeleteResult1", currentWorkoutDateTime)
            Log.d("onDeleteResult1", workoutDateTime)

            Result.success(updatedGymDay.toUiModel(getApplication()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}