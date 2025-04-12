package com.example.gymlog.ui.screens.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymlog.viewmodel.WorkoutViewModel

/**
 * Екран тренування, що відображає:
 * - Таймери (загальний та відпочинку)
 * - Керування таймерами (старт/стоп)
 * - Список підходів (сетів)
 *
 * Використовує ViewModel для бізнес-логіки (WorkoutViewModel)
 */
@Composable
fun WorkoutScreen(
    // Ін'єкція ViewModel через Hilt - не потрібно нічого передавати - Хілт сам все дасть
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    //    val state = viewModel.uiState.collectAsState().value // Неправильно!
    // Отримання стану з ViewModel як State
    val state by viewModel.uiState // Це "стіл секретаря" (стан, який зберігається у ViewModel).
        //Але це не просто звичайний об'єкт — це StateFlow (спеціальна "підписка" на зміни).
        .collectAsState()  //Це "підключення до оновлень".Кажемо Compose:
    //"Стеж за цим станом (uiState) і перемальовуй екран щоразу, коли дані змінюються!"



    // Основний макет екрану
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* ---- Секція таймерів ---- */
        // Загальний час тренування
        Text("Загальний: ${format(state.totalTimeMs)}")
        // Час відпочинку
        Text("Відпочинок: ${format(state.restTimeMs)}")

        // Кнопки керування таймерами
        Button(onClick = { viewModel.startTimers() }) {
            Text("Старт")
        }
        Button(onClick = { viewModel.stopTimers() }) {
            Text("Стоп")
        }

        /* ---- Секція списку підходів ---- */
        LazyColumn {
            items(state.sets) { set ->
                Text("Сет: ${set.name}")
            }
        }
    }
}

/**
 * Допоміжна функція для форматування часу (мс → HH:MM:SS)
 *
 * @param ms Час у мілісекундах
 * @return Рядок у форматі HH:MM:SS
 */
private fun format(ms: Long): String {
    val s = ms / 1000      // Конвертація в секунди
    val h = s / 3600       // Години
    val m = (s % 3600) / 60 // Хвилини
    val sec = s % 60        // Секунди
    return "%02d:%02d:%02d".format(h, m, sec) // Форматування з ведучими нулями
}