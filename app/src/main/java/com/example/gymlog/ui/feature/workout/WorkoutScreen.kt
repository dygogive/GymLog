package com.example.gymlog.ui.feature.workout

import android.content.Context
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymlog.presentation.viewmodel.WorkoutViewModel
import com.example.gymlog.ui.feature.workout.ui.WorkoutScreenContent
import com.example.gymlog.ui.feature.workout.ui.WorkoutSelectionDialog
import androidx.navigation.NavController
import com.example.gymlog.R
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.ui.feature.workout.model.AttributesInfo
import com.example.gymlog.ui.feature.workout.model.Equipment
import com.example.gymlog.ui.feature.workout.model.ExerciseInfo
import com.example.gymlog.ui.feature.workout.model.Motion
import com.example.gymlog.ui.feature.workout.model.Muscles
import com.example.gymlog.ui.feature.workout.model.TimerParams
import com.example.gymlog.ui.feature.workout.model.TrainingBlockInfo

@Composable
fun WorkoutScreen(
    navController: NavController,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val state by viewModel.uiState.collectAsState()

    val timerParams = TimerParams(
        totalTimeMs = state.totalTimeMs,
        lastSetTimeMs = state.lastSetTimeMs,
        "setup_needed",
        viewModel::startStopGym,
        viewModel::onSetFinish,
        state.isGymRunning
    )


    // Виправлений код для створення списку TrainingBlockInfo
    val trainingBlocksInfo = state.blocks.map { block ->
        TrainingBlockInfo(
            name = block.name,
            description = block.description,
            attributesInfo = AttributesInfo(
                motion = Motion(motions = block.motions.map { it.getDescription(context) }),
                muscle = Muscles(muscles = block.muscleGroupList.map { it.getDescription(context) }),
                equipment = Equipment(equipments = block.equipmentList.map { it.getDescription(context) })
            ),
            infoExercises = block.exercises.map { exercise ->
                ExerciseInfo(
                    name = exercise.getNameOnly(context),
                    description = exercise.description,
                    results = emptyList() // або exercise.getResults(), якщо є такі дані
                )
            }
        )
    }

    timerParams.buttonText = stringResource(if (timerParams.isRunning) R.string.stop_gym else R.string.start_gym)

    // 1) Контент тренування — завжди видно
    WorkoutScreenContent(
        timerParams = timerParams,
        infoBlocks = trainingBlocksInfo,
        {r -> viewModel.saveResult(
            r.weight,
            r.workTime,
            r.iteration,
            r.currentDate,
            r.currentTime
        ) }
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









