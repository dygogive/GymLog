// WorkoutScreen.kt
package com.example.gymlog.ui.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.gymlog.R
import com.example.gymlog.presentation.workout.ProgramSelectionState
import com.example.gymlog.presentation.workout.WorkoutCoordinatorViewModel
import com.example.gymlog.ui.feature.workout.model.*
import com.example.gymlog.ui.feature.workout.ui.WorkoutScreenContent
import com.example.gymlog.ui.feature.workout.ui.WorkoutSelectionDialog
import com.example.gymlog.ui.theme.MyAppTheme

/**
 * Головний екран тренування
 *
 * Відповідає за організацію UI елементів та делегування взаємодії до ViewModel.
 * Підтримує стани завантаження, помилок та основного вмісту.
 *
 * @param navController Контролер навігації для управління переходами між екранами
 * @param viewModel ViewModel для керування логікою та даними екрану
 */
@Composable
fun WorkoutScreen(
    navController: NavController,
    viewModel: WorkoutCoordinatorViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()



    // Відображення основного вмісту екрану або стани завантаження/помилок
    Box(modifier = Modifier.fillMaxSize()) {

       if (state.programSelectionState.showSelectionDialog) {// Відображення діалогу вибору програми тренування
            DialogOverlay(
                programSelectionState = state.programSelectionState,
                onProgramSelected = viewModel::onProgramSelected,
                onGymSelected = viewModel::onSessionSelected,
                onDismiss = {
                    viewModel.dismissSelectionDialog()
                    // Повертаємось назад тільки якщо тренування ще не розпочато
                    if (!state.trainingBlocksState.isGymDayChosen) {
                        navController.navigateUp()
                    }
                },
                onRetry = viewModel::retryLoadPrograms
            )
        } else if (state.trainingBlocksState.isGymDayChosen) { // Основний контент екрану тренування
            // Створення параметрів таймера для відображення
            val timerParams = TimerParams(
                totalTimeMs = state.timerState.totalTimeMs,
                lastSetTimeMs = state.timerState.lastSetTimeMs,
                buttonText = stringResource(
                    if (state.timerState.isGymRunning) R.string.stop_gym else R.string.start_gym
                ),
                onStartStopClick = viewModel::startStopGym,
                onSetFinished = viewModel::onSetFinish,
                isRunning = state.timerState.isGymRunning
            )



            // Відображення основного контенту тренування
            WorkoutScreenContent(
                timerParams = timerParams,
                infoBlocks = state.trainingBlocksState.blocks,
                onConfirmResult = { result ->
                    viewModel.saveResult(
                        exerciseInBlockId = result.exeInBlockId,
                        weight = result.weight,
                        iterations = result.iteration,
                        workTime = result.workTime,
                    )
                },
                expandedExeId = state.timerState.expandedExerciseId,
                onClickExpandExercise = viewModel::onClickExpandExercise,
                onDeleteResult = viewModel::onDeleteResult,
                onEditResult = viewModel::onEditResult,
            )
        }


    }
}

/**
 * Відображення діалогу вибору програми тренування з різними станами
 */
@Composable
private fun DialogOverlay(
    programSelectionState: ProgramSelectionState,
    onProgramSelected: (ProgramInfo) -> Unit,
    onGymSelected: (GymDayUiModel) -> Unit,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {

        if (programSelectionState.isLoading) {
            CircularProgressIndicator(// Відображення індикатора завантаження
                modifier = Modifier.size(48.dp)
            )
        } else if (programSelectionState.errorMessage != null) {
            ErrorContent(// Відображення повідомлення про помилку
                errorMessage = programSelectionState.errorMessage,
                onRetry = onRetry,
                onDismiss = onDismiss
            )
        } else {
            WorkoutSelectionDialog(// Відображення діалогу вибору програми
                programs = programSelectionState.availablePrograms,
                onProgramSelected = onProgramSelected,
                onGymSelected = onGymSelected,
                onDismiss = onDismiss
            )
        }
    }
}

/**
 * Відображення повідомлення про помилку з можливістю повтору
 */
@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}

// region Превью-функції для розробки

/**
 * Превью основного контенту екрану тренування
 */
@Preview(showBackground = true, name = "Превью екрану тренування")
@Composable
fun Preview_WorkoutScreenContent() {
    MyAppTheme {
        // Приклад блоку тренування для превью
        val sampleBlock = TrainingBlockUiModel(
            name = "День ніг",
            description = "Квадрицепси, Біцепси стегна",
            attributesInfo = AttributesInfo(
                motionStateList = MotionStateList(listOf("Жим", "Тяга")),
                muscleStateList = MusclesStateList(listOf("Квадрицепси", "Біцепс стегна")),
                equipmentStateList = EquipmentStateList(listOf("Штанга", "Стійка"))
            ),
            infoExercises = listOf(
                ExerciseBlockUI(0,"Присідання", "Присідання зі штангою", emptyList()),
                ExerciseBlockUI(0,"Випади", "Випади з кроком", emptyList())
            )
        )

        WorkoutScreenContent(
            timerParams = TimerParams(0L, 0L, "Почати", {}, {}, false),
            infoBlocks = listOf(sampleBlock),
            onConfirmResult = {},
            0,
            {},
            {},
            {}
        )
    }
}

/**
 * Превью діалогу вибору програми тренування
 */
@Preview(showBackground = true, name = "Превью діалогу вибору")
@Composable
fun Preview_WorkoutSelectionDialog() {
    MyAppTheme {
        WorkoutSelectionDialog(
            programs = listOf(
                ProgramInfo(0,"Початківець", "Вступна програма", listOf(
                    GymDayUiModel(0,"День 1", "Усе тіло", 0, emptyList())
                )),
                ProgramInfo(0,"Продвинута", "Інтенсивна програма", listOf(
                    GymDayUiModel(0,"День A", "Груди і спина", 0, emptyList())
                ))
            ),
            onProgramSelected = {},
            onGymSelected = {},
            onDismiss = {}
        )
    }
}

/**
 * Превью екрану помилки
 */
@Preview(showBackground = true, name = "Превью екрану помилки")
@Composable
fun Preview_ErrorContent() {
    MyAppTheme {
        ErrorContent(
            errorMessage = "Не вдалося завантажити програми тренування. Перевірте підключення до мережі.",
            onRetry = {},
            onDismiss = {}
        )
    }
}

// endregion