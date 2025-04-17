package com.example.gymlog.presentation.screens.workout

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymlog.R
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.presentation.components.TrainingBlockWorkout
import com.example.gymlog.presentation.components.createPreviewTrainingBlock
import com.example.gymlog.presentation.theme.MyAppTheme
import com.example.gymlog.presentation.viewmodel.WorkoutViewModel

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = hiltViewModel(),
    gymDayId: Long = 2L
) {
    LaunchedEffect(gymDayId) {
        viewModel.loadTrainingBlocksOnce(gymDayId)
    }

    val state by viewModel.uiState.collectAsState()

    WorkoutScreenContent(
        totalTimeMs = state.totalTimeMs,
        lastSetTimeMs = state.lastSetTimeMs,
        blocks = state.blocks,
        isRunning = state.isGymRunning,
        onStartStop = viewModel::startStopGym,
        onSetFinish = viewModel::onSetFinish
    )
}

@Composable
private fun WorkoutScreenContent(
    totalTimeMs: Long,
    lastSetTimeMs: Long,
    blocks: List<TrainingBlock>,
    isRunning: Boolean,
    onStartStop: () -> Unit,
    onSetFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonText = stringResource(if (isRunning) R.string.stop_gym else R.string.start_gym)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        TopSection(
            totalTimeMs = totalTimeMs,
            lastSetTimeMs = lastSetTimeMs,
            buttonText = buttonText,
            onStartStop = onStartStop,
            onSetFinish = onSetFinish
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            items(blocks) { block ->
                TrainingBlockWorkout(
                    block = block,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TopSection(
    totalTimeMs: Long,
    lastSetTimeMs: Long,
    buttonText: String,
    onStartStop: () -> Unit,
    onSetFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimerColumn(
            totalTimeMs = totalTimeMs,
            lastSetTimeMs = lastSetTimeMs,
            modifier = Modifier.weight(1f)
        )

        ControlsColumn(
            buttonText = buttonText,
            onStartStop = onStartStop,
            onSetFinish = onSetFinish,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TimerColumn(
    totalTimeMs: Long,
    lastSetTimeMs: Long,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerDisplay(labelRes = R.string.total_time, timeMs = totalTimeMs)
        Spacer(modifier = Modifier.height(8.dp))
        TimerDisplay(labelRes = R.string.since_last_note, timeMs = lastSetTimeMs)
    }
}

@Composable
private fun ControlsColumn(
    buttonText: String,
    onStartStop: () -> Unit,
    onSetFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onStartStop,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = buttonText)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onSetFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = stringResource(R.string.set_finished))
        }
    }
}

@Composable
private fun TimerDisplay(
    labelRes: Int,
    timeMs: Long
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = formatTime(timeMs),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

@Preview(showBackground = true, name = "WorkoutScreenContent Preview")
@Composable
private fun WorkoutScreenContentPreview() {
    MyAppTheme {
        WorkoutScreenContent(
            totalTimeMs = 1234567L,
            lastSetTimeMs = 45000L,
            blocks = List(4) { createPreviewTrainingBlock() },
            isRunning = false,
            onStartStop = {},
            onSetFinish = {}
        )
    }
}

@Preview(showBackground = true, name = "TopSection Preview")
@Composable
private fun TopSectionPreview() {
    MyAppTheme {
        TopSection(
            totalTimeMs = 3600000,
            lastSetTimeMs = 150000,
            buttonText = stringResource(R.string.start_gym),
            onStartStop = {},
            onSetFinish = {}
        )
    }
}