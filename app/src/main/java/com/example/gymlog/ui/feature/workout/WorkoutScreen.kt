package com.example.gymlog.ui.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.gymlog.R
import com.example.gymlog.presentation.state.SelectionState
import com.example.gymlog.presentation.viewmodel.WorkoutViewModel
import com.example.gymlog.ui.feature.workout.model.AttributesInfo
import com.example.gymlog.ui.feature.workout.model.EquipmentStateList
import com.example.gymlog.ui.feature.workout.model.ExerciseInfo
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import com.example.gymlog.ui.feature.workout.model.TimerParams
import com.example.gymlog.ui.feature.workout.ui.WorkoutScreenContent
import com.example.gymlog.ui.feature.workout.ui.WorkoutSelectionDialog
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import com.example.gymlog.ui.feature.workout.model.GymDayUiModel
import com.example.gymlog.ui.feature.workout.model.MotioStateList
import com.example.gymlog.ui.feature.workout.model.MusclesStateList
import com.example.gymlog.ui.feature.workout.model.TrainingBlockUiModel
import com.example.gymlog.ui.theme.MyAppTheme

/**
 * Головний екран тренування.
 */
@Composable
fun WorkoutScreen(
    navController: NavController,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    val timerParams = createTimerParams(
        totalTimeMs = state.timerState.totalTimeMs,
        lastSetTimeMs = state.timerState.lastSetTimeMs,
        isRunning = state.timerState.isGymRunning,
        onStartStop = viewModel::startStopGym,
        onSetFinish = viewModel::onSetFinish
    )

    // Блоки тренування вже готові у стейті, не потрібно конвертувати!
    val trainingBlocksInfo = state.trainingState.blocks

    WorkoutScreenContent(
        timerParams = timerParams,
        infoBlocks = trainingBlocksInfo,
        onConfirmResult = { result -> handleResultConfirmation(result, viewModel) }
    )

    if (state.selectionState.showSelectionDialog) {
        ShowSelectionDialog(
            state = state.selectionState,
            onProgramSelected = viewModel::onProgramSelected,
            onGymSelected = viewModel::onSessionSelected,
            onDismiss = {
                viewModel.dismissSelectionDialog()
                navController.navigateUp()
            }
        )
    }
}

@Composable
private fun createTimerParams(
    totalTimeMs: Long,
    lastSetTimeMs: Long,
    isRunning: Boolean,
    onStartStop: () -> Unit,
    onSetFinish: () -> Unit
): TimerParams {
    val buttonText = stringResource(
        if (isRunning) R.string.stop_gym else R.string.start_gym
    )
    return TimerParams(
        totalTimeMs = totalTimeMs,
        lastSetTimeMs = lastSetTimeMs,
        buttonText = buttonText,
        onStartStopClick = onStartStop,
        onSetFinished = onSetFinish,
        isRunning = isRunning
    )
}

private fun handleResultConfirmation(
    result: ResultOfSet,
    viewModel: WorkoutViewModel
) {
    viewModel.saveResult(
        weight = result.weight,
        iteration = result.iteration,
        workTime = result.workTime,
        date = result.currentDate,
        time = result.currentTime
    )
}

@Composable
private fun ShowSelectionDialog(
    state: SelectionState,
    onProgramSelected: (ProgramInfo) -> Unit,
    onGymSelected: (GymDayUiModel) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
    ) {
        WorkoutSelectionDialog(
            programs = state.availablePrograms,
            onProgramSelected = onProgramSelected,
            onGymSelected = onGymSelected,
            onDismiss = onDismiss
        )
    }
}





// --- Previews ---

@Preview(showBackground = true, name = "Workout Content Preview")
@Composable
fun Preview_WorkoutScreenContent() {
    MyAppTheme {
        // Provide sample blocks
        val sampleBlock = TrainingBlockUiModel(
            name = "Leg Day",
            description = "Quads, Hamstrings",
            attributesInfo = AttributesInfo(
                motionStateList = MotioStateList(listOf("Push", "Pull")),
                muscleStateList = MusclesStateList(listOf("Quads", "Hamstrings")),
                equipmentStateList = EquipmentStateList(listOf("Barbell", "Rack"))
            ),
            infoExercises = listOf(
                ExerciseInfo("Squat", "Back squat", emptyList()),
                ExerciseInfo("Lunge", "Walking lunge", emptyList())
            )
        )
        WorkoutScreenContent(
            timerParams = TimerParams(0L, 0L, "Start", {}, {} , false),
            infoBlocks = listOf(sampleBlock),
            onConfirmResult = {}
        )
    }
}

@Preview(showBackground = true, name = "Selection Dialog Preview")
@Composable
fun Preview_WorkoutSelectionDialog() {
    MyAppTheme {
        WorkoutSelectionDialog(
            programs = listOf(
                ProgramInfo("Beginner", "Intro program", listOf(
                    GymDayUiModel("Day 1", "Full body", 0, emptyList())
                )),
                ProgramInfo("Advanced", "Intense program", listOf(
                    GymDayUiModel("Day A", "Chest & Back", 0, emptyList())
                ))
            ),
            onProgramSelected = {},
            onGymSelected = {},
            onDismiss = {}
        )
    }
}
