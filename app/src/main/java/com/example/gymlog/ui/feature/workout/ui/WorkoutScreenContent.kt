package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.ui.theme.MyAppTheme


@Composable
fun WorkoutScreenContent(
    totalTimeMs: Long,
    lastSetTimeMs: Long,
    blocks: List<TrainingBlock>,
    isRunning: Boolean,
    onStartStop: () -> Unit,
    onSetFinish: () -> Unit,
    modifier: Modifier = Modifier,
    onClickFixResults: () -> Unit
) {
    val buttonText = stringResource(if (isRunning) R.string.stop_gym else R.string.start_gym)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        TimerSection(
            totalTimeMs = totalTimeMs,
            lastSetTimeMs = lastSetTimeMs,
            buttonText = buttonText,
            onStartStop = onStartStop,
            onSetFinish = onSetFinish,
            modifier = Modifier
                .fillMaxWidth()  // Змініть fillMaxSize на fillMaxWidth
                .padding(16.dp)  // Тепер horizontal padding буде працювати як очікується
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(blocks) { block ->
                TrainingBlockWorkout(
                    trainBlock = block,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClickFixResults = onClickFixResults
                )
            }
        }


    }
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
            onSetFinish = {},
            onClickFixResults = {}
        )
    }
}
