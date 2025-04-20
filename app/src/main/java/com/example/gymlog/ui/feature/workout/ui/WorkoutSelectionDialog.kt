package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymlog.R
import com.example.gymlog.domain.model.plan.FitnessProgram
import com.example.gymlog.domain.model.plan.GymDay
import com.example.gymlog.ui.theme.MyAppTheme

enum class SelectionState {
    PROGRAMS,
    GYM_SESSIONS
}



@Composable
fun WorkoutSelectionDialog(
    programs: List<FitnessProgram>,
    workoutsByProgramId: Map<Long, List<GymDay>>,
    onProgramSelected: (FitnessProgram) -> Unit,
    onGymSelected: (GymDay) -> Unit,
    onDismiss: () -> Unit
) {
    var selectionState by remember { mutableStateOf(SelectionState.PROGRAMS) }
    var selectedProgram by remember { mutableStateOf<FitnessProgram?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor   = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Title with step indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (selectionState == SelectionState.PROGRAMS)
                            stringResource(R.string.select_program)
                        else
                            stringResource(R.string.select_training),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Step indicator with click support
                StepIndicator(
                    currentStep = selectionState,
                    onStepClick = { newState ->
                        // дозволяє повертатися між станами
                        if (newState == SelectionState.PROGRAMS) {
                            selectionState = SelectionState.PROGRAMS
                        } else if (selectedProgram != null) {
                            selectionState = SelectionState.GYM_SESSIONS
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // Content based on current state
                when (selectionState) {
                    SelectionState.PROGRAMS -> {
                        ProgramsList(
                            programs = programs,
                            onProgramSelected = { program ->
                                selectedProgram = program
                                onProgramSelected(program)
                                selectionState = SelectionState.GYM_SESSIONS
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SelectionState.GYM_SESSIONS -> {
                        selectedProgram?.let { program ->
                            WorkoutsList(
                                workouts = workoutsByProgramId[program.id] ?: emptyList(),
                                onWorkoutSelected = onGymSelected,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Bottom navigation arrow (залишаємо для зручності)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    when (selectionState) {
                        SelectionState.GYM_SESSIONS -> {
                            IconButton(
                                onClick = { selectionState = SelectionState.PROGRAMS },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Назад до програм"
                                )
                            }
                        }
                        SelectionState.PROGRAMS -> {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Вийти із діалогу"
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun StepIndicator(
    currentStep: SelectionState,
    onStepClick: (SelectionState) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot для програм
        StepDot(
            isActive = currentStep == SelectionState.PROGRAMS,
            text = "Програми",
            modifier = Modifier.clickable { onStepClick(SelectionState.PROGRAMS) }
        )

        Divider(
            modifier = Modifier
                .width(24.dp)
                .padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        // Dot для тренувань
        StepDot(
            isActive = currentStep == SelectionState.GYM_SESSIONS,
            text = "Тренування",
            modifier = Modifier.clickable { onStepClick(SelectionState.GYM_SESSIONS) }
        )
    }
}

@Composable
fun StepDot(
    isActive: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = if (isActive) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ProgramsList(
    programs: List<FitnessProgram>,
    onProgramSelected: (FitnessProgram) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        items(programs) { program ->
            ProgramItem(
                program = program,
                onClick = { onProgramSelected(program) }
            )
        }
    }
}

@Composable
fun ProgramItem(
    program: FitnessProgram,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor   = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = program.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = program.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }


    }
}

@Composable
fun WorkoutsList(
    workouts: List<GymDay>,
    onWorkoutSelected: (GymDay) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        items(workouts) { workout ->
            WorkoutItem(
                workout = workout,
                onClick = { onWorkoutSelected(workout) }
            )
        }
    }
}

@Composable
fun WorkoutItem(
    workout: GymDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {


    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor   = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleMedium
            )

            workout.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "WorkoutSelectionDialog – Programs")
@Composable
fun WorkoutSelectionDialogPreview_Programs() {
    MyAppTheme {
        WorkoutSelectionDialog(
            programs = listOf(
                FitnessProgram(1L, "Початківці", "Проста базова програма", emptyList()),
                FitnessProgram(2L, "Просунуті", "Складніший план", emptyList())
            ),
            workoutsByProgramId = mapOf(
                1L to listOf(
                    GymDay(1, 1, "День 1", "Ноги й корпус", emptyList()),
                    GymDay(2, 1, "День 2", "Груди й спина", emptyList())
                ),
                2L to listOf(
                    GymDay(3, 2, "День A", "Повне тіло", emptyList()),
                    GymDay(4, 2, "День B", "Кардіо", emptyList())
                )
            ),
            onProgramSelected = {},
            onGymSelected = {},
            onDismiss = {}
        )
    }
}
