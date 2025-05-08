package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.BorderStroke
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
import com.example.gymlog.ui.feature.workout.model.GymDayUiModel
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import com.example.gymlog.ui.theme.MyAppTheme

enum class SelectionStep { PROGRAMS, GYM_SESSIONS }

@Composable
fun WorkoutSelectionDialog(
    programs: List<ProgramInfo>,
    onProgramSelected: (ProgramInfo) -> Unit,
    onGymSelected: (GymDayUiModel) -> Unit,
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(SelectionStep.PROGRAMS) }
    var selectedProgram by remember { mutableStateOf<ProgramInfo?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.75f),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp
        ) {
            WorkoutSelectionContent(
                step = step,
                selectedProgram = selectedProgram,
                programs = programs,
                onProgramSelected = { it ->
                    selectedProgram = it
                    step = SelectionStep.GYM_SESSIONS
                    onProgramSelected(it)
                },
                onWorkoutSelected = onGymSelected,
                onBack = { step = SelectionStep.PROGRAMS },
                onDismiss = onDismiss,
                onStepClick = { newStep ->
                    when (newStep) {
                        SelectionStep.PROGRAMS -> step = SelectionStep.PROGRAMS
                        SelectionStep.GYM_SESSIONS -> if (selectedProgram != null) step = SelectionStep.GYM_SESSIONS
                    }
                }
            )
        }
    }
}

@Composable
private fun WorkoutSelectionContent(
    step: SelectionStep,
    selectedProgram: ProgramInfo?,
    programs: List<ProgramInfo>,
    onProgramSelected: (ProgramInfo) -> Unit,
    onWorkoutSelected: (GymDayUiModel) -> Unit,
    onBack: () -> Unit,
    onDismiss: () -> Unit,
    onStepClick: (SelectionStep) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Header(step, onBack, onDismiss)
        StepIndicator(
            currentStep = step,
            canGoToGymSessions = selectedProgram != null,
            onStepClick = onStepClick
        )
        when (step) {
            SelectionStep.PROGRAMS -> ProgramsList(
                programs = programs,
                onProgramSelected = onProgramSelected,
                modifier = Modifier.weight(1f)
            )
            SelectionStep.GYM_SESSIONS -> selectedProgram?.let { program ->
                WorkoutsList(
                    workouts = program.gymDayUiModels,
                    onWorkoutSelected = onWorkoutSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun Header(
    step: SelectionStep,
    onBack: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (step == SelectionStep.GYM_SESSIONS) onBack() else onDismiss() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = if (step == SelectionStep.PROGRAMS)
                    stringResource(R.string.select_program)
                else
                    stringResource(R.string.select_training),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StepIndicator(
    currentStep: SelectionStep,
    canGoToGymSessions: Boolean,
    onStepClick: (SelectionStep) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepDot(
                isActive = currentStep == SelectionStep.PROGRAMS,
                text = stringResource(R.string.programs),
                modifier = Modifier.clickable { onStepClick(SelectionStep.PROGRAMS) }
            )
            HorizontalDivider(
                modifier = Modifier
                    .width(24.dp)
                    .padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            StepDot(
                isActive = currentStep == SelectionStep.GYM_SESSIONS,
                text = stringResource(R.string.gym_sessions),
                modifier = Modifier.clickable(
                    enabled = canGoToGymSessions
                ) { onStepClick(SelectionStep.GYM_SESSIONS) },
                enabled = canGoToGymSessions
            )
        }
    }
}

@Composable
fun StepDot(
    isActive: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
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
                    when {
                        isActive -> MaterialTheme.colorScheme.primary
                        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = when {
                isActive -> MaterialTheme.colorScheme.primary
                !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            },
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ProgramsList(
    programs: List<ProgramInfo>,
    onProgramSelected: (ProgramInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    if (programs.isEmpty()) {
        EmptyListMessage(message = stringResource(R.string.no_programs))
    } else {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(programs) { program ->
                ProgramItem(program, onProgramSelected)
            }
        }
    }
}

@Composable
fun ProgramItem(
    program: ProgramInfo,
    onClick: (ProgramInfo) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(program) },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                program.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            program.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun WorkoutsList(
    workouts: List<GymDayUiModel>,
    onWorkoutSelected: (GymDayUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (workouts.isEmpty()) {
        EmptyListMessage(message = stringResource(R.string.no_workouts))
    } else {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(workouts) { workout ->
                WorkoutItem(workout, onWorkoutSelected)
            }
        }
    }
}

@Composable
fun WorkoutItem(
    workout: GymDayUiModel,
    onClick: (GymDayUiModel) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(workout) },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                workout.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            workout.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyListMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutSelectionDialogPreview() {
    val dummyPrograms = listOf(
        ProgramInfo(
            name = "Program A",
            description = "Description A",
            gymDayUiModels = listOf(
                GymDayUiModel(
                    name = "Workout 1",
                    description = "Chest Day",
                    position = 1,
                    trainingBlocksUiModel = emptyList()
                ),
                GymDayUiModel(
                    name = "Workout 2",
                    description = "Leg Day",
                    position = 2,
                    trainingBlocksUiModel = emptyList()
                )
            )
        ),
        ProgramInfo(
            name = "Program B",
            description = "Description B",
            gymDayUiModels = listOf(
                GymDayUiModel(
                    name = "Workout 3",
                    description = "Back Day",
                    position = 1,
                    trainingBlocksUiModel = emptyList()
                )
            )
        )
    )

    MyAppTheme(useDarkTheme = false) {
        Box(Modifier.fillMaxSize()) {
            WorkoutSelectionDialog(
                programs = dummyPrograms,
                onProgramSelected = {},
                onGymSelected = {},
                onDismiss = {}
            )
        }
    }
}