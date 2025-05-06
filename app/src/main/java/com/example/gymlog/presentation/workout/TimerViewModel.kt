package com.example.gymlog.presentation.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для керування таймером тренування.
 * Відповідає за відстеження загального часу тренування та часу окремих підходів.
 */
class TimerViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    // Стан таймера для UI
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    // Змінні для відстеження часу
    private var timerJob: Job? = null        // Job корутини для таймера
    private var startWorkoutTime = 0L        // час початку тренування в мілісекундах
    private var startSetTime = 0L            // час початку підходу в мілісекундах

    /**
     * Запускає або зупиняє тренування в залежності від поточного стану.
     * Якщо тренування активне - зупиняє його, інакше - запускає.
     */
    fun startStopGym() {
        if (timerState.value.isGymRunning) {
            stopGym()
        } else {
            startGym()
        }
    }

    /**
     * Запускає таймер тренування та підходу.
     * Оновлює поточний час тренування кожну секунду.
     */
    private fun startGym() {
        // Запам'ятовуємо час початку тренування і підходу
        startWorkoutTime = System.currentTimeMillis()
        startSetTime = startWorkoutTime

        // Оновлюємо стан таймера - починаємо відлік
        _timerState.update { timerState ->
            timerState.copy(
                isGymRunning = true,  // Позначаємо, що тренування активне
                totalTimeMs = 0L,     // Скидаємо загальний час тренування
                lastSetTimeMs = 0L    // Скидаємо час поточного підходу
            )
        }

        // Скасовуємо попередній таймер, якщо він був
        timerJob?.cancel()

        // Запускаємо нову корутину таймера
        timerJob = viewModelScope.launch {
            // Цикл оновлення часу, поки тренування активне
            while (timerState.value.isGymRunning) {
                val currentTimeMs = System.currentTimeMillis()

                // Оновлюємо час тренування і час підходу
                _timerState.update { timerState ->
                    timerState.copy(
                        totalTimeMs = currentTimeMs - startWorkoutTime,   // Загальний час тренування
                        lastSetTimeMs = currentTimeMs - startSetTime      // Час поточного підходу
                    )
                }

                // Затримка в 1 секунду перед наступним оновленням
                delay(1000L)
            }
        }
    }

    /**
     * Фіксує закінчення підходу і скидає таймер для поточного підходу.
     * Викликається коли користувач закінчив один підхід вправи.
     */
    fun onSetFinish() {
        // Запам'ятовуємо час початку нового підходу
        startSetTime = System.currentTimeMillis()
        // Скидаємо час підходу на 0
        _timerState.update { timerState ->
            timerState.copy(lastSetTimeMs = 0L)
        }
    }

    /**
     * Зупиняє таймер тренування.
     * Скасовує оновлення часу і змінює стан на "не активний".
     */
    private fun stopGym() {
        // Скасовує корутину таймера
        timerJob?.cancel()

        // Оновлює стан таймера на "зупинено"
        _timerState.update { timerState ->
            timerState.copy(isGymRunning = false)
        }
    }

    /**
     * Повертає поточний час тренування в мілісекундах
     */
    fun getCurrentWorkoutTimeMs(): Long {
        return timerState.value.totalTimeMs
    }

    /**
     * Викликається при знищенні ViewModel.
     * Важливо скасувати всі корутини для запобігання витоку пам'яті.
     */
    override fun onCleared() {
        super.onCleared()
        // Скасування таймера при знищенні ViewModel
        timerJob?.cancel()
    }
}