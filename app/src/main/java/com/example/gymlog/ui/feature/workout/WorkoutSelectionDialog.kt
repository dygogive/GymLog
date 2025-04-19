package com.example.gymlog.ui.feature.workout

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymlog.domain.model.plan.FitnessProgram
import com.example.gymlog.domain.model.plan.GymSession

enum class SelectionState {
    PROGRAMS,
    GYM_SESSIONS
}

@Composable
fun WorkoutSelectionDialog(
    programs: List<FitnessProgram>,
    workoutsByProgram: Map<Long, List<GymSession>>,
    onProgramSelected: (FitnessProgram) -> Unit,
    onWorkoutSelected: (GymSession) -> Unit,
    onDismiss: () -> Unit
) {
    var selectionState by remember { mutableStateOf(SelectionState.PROGRAMS) }
    var selectedProgram by remember { mutableStateOf<FitnessProgram?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f),
            shape = RoundedCornerShape(16.dp),
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
                            "Виберіть програму тренувань"
                        else "Виберіть тренування",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Step indicator
                StepIndicator(
                    currentStep = selectionState,
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
                                selectionState = SelectionState.GYM_SESSIONS
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SelectionState.GYM_SESSIONS -> {
                        selectedProgram?.let { program ->
                            WorkoutsList(
                                workouts = workoutsByProgram[program.id] ?: emptyList(),
                                onWorkoutSelected = onWorkoutSelected,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Bottom navigation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (selectionState == SelectionState.GYM_SESSIONS) {
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
                }
            }
        }
    }
}

@Composable
fun StepIndicator(
    currentStep: SelectionState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepDot(
            isActive = currentStep == SelectionState.PROGRAMS,
            text = "Програми"
        )

        Divider(
            modifier = Modifier
                .width(24.dp)
                .padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        StepDot(
            isActive = currentStep == SelectionState.GYM_SESSIONS,
            text = "Тренування"
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
    workouts: List<GymSession>,
    onWorkoutSelected: (GymSession) -> Unit,
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
    workout: GymSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
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