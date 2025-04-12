// Оголошення пакету
package com.example.gymlog.viewmodel

// Імпорт необхідних бібліотек і класів
import androidx.lifecycle.ViewModel                // Базовий клас ViewModel
import androidx.lifecycle.viewModelScope          // Coroutine Scope для ViewModel

import com.example.gymlog.data.repository.WorkoutRepository  // Репозиторій для роботи з даними
import com.example.gymlog.ui.screens.workout.WorkoutUiState  // Клас стану UI

import dagger.hilt.android.lifecycle.HiltViewModel // Аннотація для Hilt DI

import kotlinx.collections.immutable.toPersistentList // Конвертація у незмінний список

import kotlinx.coroutines.Job                    // Об'єкт для керування корутинами
import kotlinx.coroutines.delay                  // Функція затримки
import kotlinx.coroutines.flow.*                 // Імпорт потоків
import kotlinx.coroutines.launch                 // Запуск корутини

import javax.inject.Inject                       // Аннотація для ін'єкції залежностей

import kotlin.time.Duration.Companion.seconds    // Конвертація часу

// Аннотація для автоматичної генерації залежностей через Hilt
@HiltViewModel // Оголошення класу ViewModel з ін'єкцією репозиторію через конструктор
class WorkoutViewModel @Inject constructor(
    private val repo: WorkoutRepository  // Ін'єкція репозиторію Хілтом (Hilt)
) : ViewModel() {  // Наслідування від базового ViewModel

    /* ---------------- State ---------------- */

    // Приватний MutableStateFlow для внутрішнього керування станом
    private val _uiState = MutableStateFlow(WorkoutUiState())
    // Публічний StateFlow для спостереження стану з UI (в класі UI ми його використовуємо)
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    /* ---------------- Таймери ---------------- */

    // Об'єкт Job для керування корутиною таймера
    private var timerJob: Job? = null
    // Час початку тренування (мс)
    private var startTs = 0L
    // Час початку відпочинку (мс)
    private var restStartTs = 0L

    // Функція запуску таймерів
    fun startTimers() {
        // Фіксуємо поточний час як час старту
        startTs = System.currentTimeMillis()
        restStartTs = startTs
        // Скасовуємо попередню корутину таймера, якщо воно було
        timerJob?.cancel()
        // Запускаємо нову корутину для таймера
        timerJob = viewModelScope.launch {
            // Безкінечний цикл оновлення часу
            while (true) {
                //нове значення часу
                val now = System.currentTimeMillis()  // Поточний час
                // Оновлюємо стан з новими значеннями часу
                _uiState.update {  //Автоматично блокує конкурентні оновлення (потокобезпечний)
                    //Надає доступ до поточного значення стану через параметр it
                    it.copy( //Створює нову копію об'єкта стану
                        //Дозволяє змінити тільки потрібні поля
                        totalTimeMs = now - startTs,    // Загальний час = поточний час - час старту
                        lastSetTimeMs = now - restStartTs  // Час відпочинку = поточний час - час старту відпочинку
                    )
                    //Замінює стан на нове значення, яке повертається з лямбди
                }
                // Затримка 1 секунда перед наступним оновленням
                delay(1.seconds)

                /**
                 * Аналогічний код без використання update і copy:
                 * val currentState = _uiState.value
                 * val newState = currentState.copy(
                 *     totalTimeMs = now - startTs,
                 *     restTimeMs = now - restStartTs
                 * )
                 * _uiState.value = newState
                 * */


            }
        }
    }

    // Функція зупинки таймерів
    fun stopTimers() {
        timerJob?.cancel()  // Скасовуємо корутину таймера
    }

    // Функція скидання таймера відпочинку
    fun resetRestTimer() {
        restStartTs = System.currentTimeMillis()  // Оновлюємо час старту відпочинку
    }

    /* ---------------- Завантаження даних ---------------- */

    // Функція спостереження за підходами для конкретного дня
    fun observeSetsForDay(dayId: Long) {
        // Запускаємо корутину в viewModelScope (автоматично скасовується при знищенні ViewModel)
        viewModelScope.launch {
            // Отримуємо Flow (підписка на інформацію) з репозиторію
            repo.getSetsForDay(dayId)
                //даємо задачу кур'єрській доставці приносити нову інформацію якщо вона з'явиться
                .collect { list ->  // Кур’єр чекає біля поштової скриньки і відразу несе новий журнал (дані) до тебе, як тільки він приходить.
                    // Ти (ViewModel) береш новий список сетів з журналу і кладеш його на стіл (_uiState).
                    _uiState.update { it.copy(sets = list.toPersistentList()) } //Чому toPersistentList()? Це як заламінувати журнал перед тим, як покласти на стіл:
                }
            /**
             * Хто і коли скасовує підписку?
             *
             * Секретар (viewModelScope) сам це робить при звільненні (знищенні ViewModel).
             *
             * Без цього кур’єр (корутина) вічно стояв би біля скриньки, витрачаючи ресурси.
             * */
        }
    }
}