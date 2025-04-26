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
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f)
        ) {
            WorkoutSelectionContent(
                step = step,
                selectedProgram = selectedProgram,
                programs = programs,
                onProgramSelected = {
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
        Header(step)
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
        FooterNavigation(step, onBack, onDismiss)
    }
}

@Composable
private fun Header(step: SelectionStep) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        Text(
            text = if (step == SelectionStep.PROGRAMS)
                stringResource(R.string.select_program)
            else
                stringResource(R.string.select_training),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun FooterNavigation(step: SelectionStep, onBack: () -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(
            onClick = { if (step == SelectionStep.GYM_SESSIONS) onBack() else onDismiss() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepDot(
            isActive = currentStep == SelectionStep.PROGRAMS,
            text = stringResource(R.string.programs),
            modifier = Modifier.clickable { onStepClick(SelectionStep.PROGRAMS) }
        )
        Divider(
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
            ) { onStepClick(SelectionStep.GYM_SESSIONS) }
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
    programs: List<ProgramInfo>,
    onProgramSelected: (ProgramInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    if (programs.isEmpty()) {
        EmptyListMessage(message = stringResource(R.string.no_programs))
    } else {
        LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
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
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(program) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(program.name, style = MaterialTheme.typography.titleMedium)
            program.description?.let {
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

@Composable
fun WorkoutsList(
    workouts: List<GymDayUiModel>,
    onWorkoutSelected: (GymDayUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (workouts.isEmpty()) {
        EmptyListMessage(message = stringResource(R.string.no_workouts))
    } else {
        LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
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
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick(workout) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(workout.name, style = MaterialTheme.typography.titleMedium)
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

@Composable
fun EmptyListMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
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
                    trainingBlockUiModels = emptyList()
                ),
                GymDayUiModel(
                    name = "Workout 2",
                    description = "Leg Day",
                    position = 2,
                    trainingBlockUiModels = emptyList()
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
                    trainingBlockUiModels = emptyList()
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
