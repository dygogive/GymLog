package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.ui.feature.workout.model.TimerParams
import com.example.gymlog.core.utils.formatTime
import kotlin.Long

@Composable
fun TimerSection(
    timerParams: TimerParams,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer displays in a row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimerDisplay(
                    label = stringResource(R.string.total_time),
                    timeMs = timerParams.totalTimeMs,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Vertical divider
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )

                Spacer(modifier = Modifier.width(16.dp))

                TimerDisplay(
                    label = stringResource(R.string.since_last_note),
                    timeMs = timerParams.lastSetTimeMs,
                    modifier = Modifier.weight(1f)
                )
            }

            // Controls in a row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubtleButton(
                    text = timerParams.buttonText,
                    onClick = timerParams.onStartStopClick,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                SubtleButton(
                    text = stringResource(R.string.set_finished),
                    onClick = timerParams.onSetFinished,
                    modifier = Modifier.weight(1f),
                    isSecondary = true
                )
            }
        }
    }
}

@Composable
private fun TimerDisplay(
    label: String,
    timeMs: Long,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = formatTime(timeMs),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Light
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SubtleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false
) {
    val backgroundColor = if (isSecondary) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    }

    val textColor = if (isSecondary) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.primary
    }

    val borderColor = if (isSecondary) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}