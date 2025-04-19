package com.example.gymlog.ui.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymlog.R
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.presentation.viewmodel.WorkoutViewModel
import com.example.gymlog.ui.feature.workout.ui.TimerSection
import com.example.gymlog.ui.feature.workout.ui.TrainingBlockWorkout
import com.example.gymlog.ui.feature.workout.ui.WorkoutScreenContent
import com.example.gymlog.ui.feature.workout.ui.createPreviewTrainingBlock
import com.example.gymlog.ui.theme.MyAppTheme

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Поки користувач не вибрав усе — показуємо діалог
    if (state.showSelectionDialog) {
        WorkoutSelectionDialog(
            programs = state.availablePrograms,
            workoutsByProgram = state.availableGymSessions,
            onProgramSelected = viewModel::onProgramSelected,
            onWorkoutSelected = viewModel::onSessionSelected,
            onDismiss = { /*можете дозволити закриття, якщо потрібно*/ }
        )
        return  // не рендеримо основний UI поки не вибрано
    }

    // Після вибору — показ активності
    state.selectedGymSession?.let { session ->
        // LaunchedEffect з gymDayId тепер із вибраної сесії
        LaunchedEffect(session.id) {
            viewModel.loadTrainingBlocksOnce(session.id)
        }

        WorkoutScreenContent(
            totalTimeMs = state.totalTimeMs,
            lastSetTimeMs = state.lastSetTimeMs,
            blocks = state.blocks,
            isRunning = state.isGymRunning,
            onStartStop = viewModel::startStopGym,
            onSetFinish = viewModel::onSetFinish
        )
    }
}





