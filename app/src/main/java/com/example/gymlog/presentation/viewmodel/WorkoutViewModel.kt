// Оголошення пакету
package com.example.gymlog.presentation.viewmodel

// Імпорт необхідних бібліотек і класів
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel                // Базовий клас ViewModel
import androidx.lifecycle.viewModelScope          // Coroutine Scope для ViewModel
import com.example.gymlog.data.repository.TrainingBlockRepository
import com.example.gymlog.data.repository.WorkoutRepository

import com.example.gymlog.presentation.screens_compose.workout.WorkoutUiState  // Клас стану UI

import dagger.hilt.android.lifecycle.HiltViewModel // Аннотація для Hilt DI

import kotlinx.collections.immutable.toPersistentList // Конвертація у незмінний список

import kotlinx.coroutines.Job                    // Об'єкт для керування корутинами
import kotlinx.coroutines.delay                  // Функція затримки
import kotlinx.coroutines.flow.*                 // Імпорт потоків
import kotlinx.coroutines.launch                 // Запуск корутини

import javax.inject.Inject                       // Аннотація для ін'єкції залежностей

import kotlin.time.Duration.Companion.seconds    // Конвертація часу

import com.example.gymlog.R

// Аннотація для автоматичної генерації залежностей через Hilt
@HiltViewModel // Оголошення класу ViewModel з ін'єкцією репозиторію через конструктор
class WorkoutViewModel @Inject constructor(
    private val trBlkRepo: TrainingBlockRepository,  // Ін'єкція репозиторію Хілтом (Hilt)
    private val wrkOutRepo: WorkoutRepository       // Ін'єкція репозиторію Хілтом (Hilt)
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
    private var startWorkoutTime = 0L
    // Час початку нового сету (мс)
    private var startSetTime = 0L
    //текст кнопки
    var textButton = "start"


    // Функція запуску таймерів
    fun startStopGym() {
        //переведення стану тренування у активне якщо не активне
        if(!_uiState.value.isGymRunning) {
            _uiState.update { it.copy(isGymRunning = true) }
        } else {
            onStop()
            return
        }


        // Фіксуємо поточний час як час старту
        startWorkoutTime = System.currentTimeMillis()
        startSetTime = startWorkoutTime
        textButton = "stop"

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
                        totalTimeMs = now - startWorkoutTime,    // Загальний час = поточний час - час старту
                        lastSetTimeMs = now - startSetTime,  // Час відпочинку = поточний час - час старту відпочинку
                        textButtonStartStop = textButton
                    )
                    //Замінює стан на нове значення, яке повертається з лямбди
                }
                // Затримка 1 секунда перед наступним оновленням
                delay(1.seconds)
            }
        }
    }


    // Функція зупинки таймерів
    fun onStop() {
        textButton = "start"
        timerJob?.cancel()  // Скасовуємо корутину таймера
        //тренування зупинити
        _uiState.update { it.copy(
            isGymRunning = false,
            textButtonStartStop = textButton
        ) }
    }

    // Функція зупинки таймерів
    fun onSetFinish() {

        startSetTime = System.currentTimeMillis()

        _uiState.update { it.copy(lastSetTimeMs = System.currentTimeMillis() - startSetTime) }
    }










    /* ---------------- Завантаження даних ---------------- */

    //одногразовий запит до бази
    //Оновити  val blocks: PersistentList<TrainingBlock> у WorkoutUiState
    fun loadTrainingBlocksOnce(gymDayID: Long) {
        viewModelScope.launch {
            val blocks = trBlkRepo.getTrainingBlocksByDayId(gymDayID)
            _uiState.update { it.copy(blocks = blocks.toPersistentList()) }
        }
    }

//    // Функція спостереження за підходами для конкретного дня
//    fun observeTrainingBlocks(gymDayID: Long) {
//        // Запускаємо корутину в viewModelScope (автоматично скасовується при знищенні ViewModel)
//
//        var observerTrainingBlocks: Job? = viewModelScope.launch {
//            // Отримуємо Flow (підписка на інформацію) з репозиторію
//            trBlkRepo.observeTrainingBlocksByDayId(gymDayID)
//                //даємо задачу кур'єрській доставці приносити нову інформацію якщо вона з'явиться
//                .collect { list ->  // Кур’єр чекає біля поштової скриньки і відразу несе новий журнал (дані) до тебе, як тільки він приходить.
//                    // Ти (ViewModel) береш новий список сетів з журналу і кладеш його на стіл (_uiState).
//                    _uiState.update { it.copy(blocks = list.toPersistentList()) } //Чому toPersistentList()? Це як заламінувати журнал перед тим, як покласти на стіл:
//                }
//
//        }
//    }

}