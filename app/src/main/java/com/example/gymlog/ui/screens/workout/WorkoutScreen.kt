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


@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* ---- Таймери ---- */
        Text("Загальний: ${format(state.totalTimeMs)}")
        Text("Відпочинок: ${format(state.restTimeMs)}")

        Button(onClick = { viewModel.startTimers() }) { Text("Старт") }
        Button(onClick = { viewModel.stopTimers() })  { Text("Стоп")  }

        /* ---- Список сетів ---- */
        LazyColumn {
            items(state.sets) { set ->
                Text("Сет: ${set.name}")
            }
        }
    }
}


private fun format(ms: Long): String {
    val s = ms / 1000
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return "%02d:%02d:%02d".format(h, m, sec)
}
