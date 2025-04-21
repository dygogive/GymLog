package com.example.gymlog.ui.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymlog.presentation.viewmodel.WorkoutViewModel
import com.example.gymlog.ui.feature.workout.ui.WorkoutScreenContent
import com.example.gymlog.ui.feature.workout.ui.WorkoutSelectionDialog
import androidx.navigation.NavController

@Composable
fun WorkoutScreen(
    navController: NavController,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()


// 1) Контент тренування — завжди видно
    WorkoutScreenContent(
        totalTimeMs = state.totalTimeMs,
        lastSetTimeMs = state.lastSetTimeMs,
        blocks = state.blocks,
        lastWorkoutExercises = state.lastWorkoutExercises(),
        currentWorkoutExercises = state.currentWorkoutExercises(),
        isRunning = state.isGymRunning,
        onStartStop = viewModel::startStopGym,
        onSetFinish = viewModel::onSetFinish
    )


    // 2) Якщо потрібно вибір — накладаємо діалог зверху
    if (state.showSelectionDialog) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
        ) {
            WorkoutSelectionDialog(
                programs = state.availablePrograms,
                workoutsByProgramId = state.availableGymDaySessions,
                onProgramSelected = viewModel::onProgramSelected,
                onGymSelected = viewModel::onSessionSelected,
                onDismiss = {
                    // 1) приховуємо UI діалогу
                    viewModel.dismissSelectionDialog()
                    // 2) відразу повертаємось назад
                    navController.navigateUp()
                }
            )
        }
    }
}





