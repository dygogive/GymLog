package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.ui.feature.workout.model.TimerParams
import com.example.gymlog.ui.theme.MyAppTheme
import com.example.gymlog.core.utils.formatTime
import kotlin.Long

@Composable
fun TimerSection(
    timerParams: TimerParams,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,  // <- дефолт
    modifier: Modifier = Modifier,  // Цей параметр має використовуватись
) {
    Row(
        modifier = modifier  // <-- Використовуємо переданий модифікатор
            .fillMaxWidth()
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimerColumn(
            totalTimeMs = timerParams.totalTimeMs,
            lastSetTimeMs = timerParams.lastSetTimeMs,
            modifier = Modifier.weight(1f)
        )

        ControlsColumn(
            buttonText = timerParams.buttonText,
            onStartStop = timerParams.onStartStop,
            onSetFinish = timerParams.onSetFinish,
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
                .wrapContentSize()
        ) {
            Text(text = buttonText)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onSetFinish,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
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




