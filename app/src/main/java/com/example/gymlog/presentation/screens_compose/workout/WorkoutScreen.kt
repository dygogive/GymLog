package com.example.gymlog.presentation.screens_compose.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymlog.R
import com.example.gymlog.data.local.room.entity.TrainingBlockEntity
import com.example.gymlog.data.local.room.entity.WorkoutSetEntity
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.presentation.components.TrainingBlockWorkout
import com.example.gymlog.presentation.components.TrainingBlockWorkoutPreview
import com.example.gymlog.presentation.components.createPreviewTrainingBlock
import com.example.gymlog.presentation.theme.MyAppTheme
import com.example.gymlog.presentation.viewmodel.WorkoutViewModel

/**
 * Основний екран тренування. Використовує [WorkoutViewModel] для отримання актуальних даних
 * і передає їх до спільного UI-компонента [WorkoutScreenContent].
 */
@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = hiltViewModel(),
    gymDayId: Long = 2L // або приймай параметр, якщо потрібен динамічно
) {
    // Викликаємо функцію завантаження тренувальних блоків один раз при старті цього екрану.
    LaunchedEffect(key1 = gymDayId) {
        //load training blockList to view model
        viewModel.loadTrainingBlocksOnce(gymDayId)
    }
    val state by viewModel.uiState.collectAsState()



    WorkoutScreenContent(
        totalTimeMs = state.totalTimeMs,
        lastSetTimeMs = state.lastSetTimeMs,
        trainingBlockList = state.blocks,
        onStart = { viewModel.startGym() },
        onStop  = { viewModel.stopGym()  }
    )
}






/**
 * Спільний UI-компонент, що відповідає за відображення:
 * - Таймерів (загальний час і час відпочинку)
 * - Кнопок старт/стоп
 * - Списку підходів (сетів)
 *
 * @param totalTimeMs Загальний час тренування у мілісекундах.
 * @param lastSetTimeMs Час відпочинку після останнього підходу у мілісекундах.
 * @param trainingBlockList Список підходів типу [WorkoutSetEntity].
 * @param onStart Callback для старту таймерів.
 * @param onStop Callback для зупинки таймерів.
 * @param modifier Додатковий модифікатор для налаштування UI.
 */
@Composable
fun WorkoutScreenContent(
    totalTimeMs: Long,
    lastSetTimeMs: Long,
    trainingBlockList: List<TrainingBlock>,
    onStart: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Константи для відступів
    val screenPadding = 16.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Верхня секція: таймери та кнопки
        Row(
            modifier = Modifier
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(screenPadding)
                .weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Секція таймерів
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Відображення загального часу
                TimerDisplay(
                    label = stringResource(R.string.total_time),
                    time = format(totalTimeMs)
                )
                Spacer(modifier = Modifier.height(screenPadding))
                // Відображення часу відпочинку
                TimerDisplay(
                    label = stringResource(R.string.since_last_set_time),
                    time = format(lastSetTimeMs)
                )
            }

            // Секція кнопок керування
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onStart,
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(text = stringResource(R.string.start_gym))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onStop,
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(text = stringResource(R.string.new_count))
                }
            }
        }

        // Секція списку підходів (сетів)
        LazyColumn(
            modifier = Modifier.weight(2f),
        ) {
            this.items<TrainingBlock>(trainingBlockList) { trBlock ->
                TrainingBlockWorkout(
                    trBlock,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/**
 * Допоміжний UI-компонент для відображення таймера.
 *
 * @param label Заголовок таймера.
 * @param time Відформатований рядок з часом.
 */
@Composable
fun TimerDisplay(
    label: String,
    time: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = time,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}




/**
 * Форматує час з мілісекунд у рядок формату HH:MM:SS.
 *
 * @param ms Час у мілісекундах.
 * @return Рядок у форматі HH:MM:SS.
 */
private fun format(ms: Long): String {
    val seconds = ms / 1000
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, secs)
}

/**
 * Preview-версія екрану з хардкорними даними.
 * Використовує [WorkoutScreenContent] для відображення UI у темному режимі.
 * @Preview(
 *     showBackground = true,
 *     device = Devices.,
 *     apiLevel = 35,
 *     showSystemUi = true,
 *     uiMode = android.content.res.Configuration.UI_MODE_TYPE_NORMAL or
 *             android.content.res.Configuration.UI_MODE_TYPE_NORMAL
 * )
 */
@Preview(device = "id:Nexus 6P")
@Composable
fun WorkoutScreenPreview() {
    MyAppTheme {
        WorkoutScreenContent(
            totalTimeMs = 1234567,
            lastSetTimeMs = 45000,
            trainingBlockList = listOf(
                createPreviewTrainingBlock(),
                createPreviewTrainingBlock(),
                createPreviewTrainingBlock(),
                createPreviewTrainingBlock(),
            ),
            onStart = { /* Дії в прев'ю не виконуються */ },
            onStop = { /* Дії в прев'ю не виконуються */ }
        )
    }
}
