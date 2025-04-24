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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.gymlog.R
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.presentation.viewmodel.WorkoutViewModel
import com.example.gymlog.ui.feature.workout.model.AttributesInfo
import com.example.gymlog.ui.feature.workout.model.Equipment
import com.example.gymlog.ui.feature.workout.model.ExerciseInfo
import com.example.gymlog.ui.feature.workout.model.Motion
import com.example.gymlog.ui.feature.workout.model.Muscles
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import com.example.gymlog.ui.feature.workout.model.TimerParams
import com.example.gymlog.ui.feature.workout.model.TrainingBlockInfo
import com.example.gymlog.ui.feature.workout.ui.WorkoutScreenContent
import com.example.gymlog.ui.feature.workout.ui.WorkoutSelectionDialog

/**
 * Головний екран тренування.
 * Відповідає за:
 * 1. Відображення таймера та блоків тренування
 * 2. Показ діалогу вибору програми/дня тренування
 * 3. Запис результатів підходів
 */
@Composable
fun WorkoutScreen(
    navController: NavController,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    // Створюємо об'єкт параметрів таймера для UI
    val timerParams = createTimerParams(state.timerState.totalTimeMs,
        state.timerState.lastSetTimeMs,
        state.timerState.isGymRunning,
        viewModel::startStopGym,
        viewModel::onSetFinish)

    // Перетворюємо доменні блоки тренування у формат UI
    val trainingBlocksInfo = mapTrainingBlocksToUi(state.trainingState.blocks, context)

    // 1) Контент тренування — завжди видно
    WorkoutScreenContent(
        timerParams = timerParams,
        infoBlocks = trainingBlocksInfo,
        onConfirmResult = { result -> handleResultConfirmation(result, viewModel) }
    )

    // 2) Якщо потрібно вибір — накладаємо діалог зверху
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

/**
 * Створює параметри таймера для UI компонента
 */
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

/**
 * Перетворює доменні об'єкти TrainingBlock у UI представлення
 */
private fun mapTrainingBlocksToUi(
    blocks: List<TrainingBlock>,
    context: android.content.Context
): List<TrainingBlockInfo> {
    return blocks.map { block ->
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
                    results = emptyList() // TODO: завантажувати історичні результати
                )
            }
        )
    }
}

/**
 * Обробляє підтвердження результату підходу
 */
private fun handleResultConfirmation(
    result: ResultOfSet,
    viewModel: WorkoutViewModel
) {
    viewModel.saveResult(
        weight = result.weight,
        iteration = result.iteration,
        workTime = result.workTime,
        currentDate = result.currentDate,
        currentTime = result.currentTime
    )
}

/**
 * Показує діалог вибору програми та дня тренування
 */
@Composable
private fun ShowSelectionDialog(
    state: com.example.gymlog.presentation.state.SelectionState,
    onProgramSelected: (com.example.gymlog.domain.model.plan.FitnessProgram) -> Unit,
    onGymSelected: (com.example.gymlog.domain.model.plan.GymDay) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
    ) {
        WorkoutSelectionDialog(
            programs = state.availablePrograms,
            workoutsByProgramId = state.availableGymDaySessions,
            onProgramSelected = onProgramSelected,
            onGymSelected = onGymSelected,
            onDismiss = onDismiss
        )
    }
}