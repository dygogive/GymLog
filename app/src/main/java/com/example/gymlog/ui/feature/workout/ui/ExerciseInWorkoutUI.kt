package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymlog.R
import com.example.gymlog.ui.feature.workout.model.ExerciseBlockUI
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import com.example.gymlog.core.utils.getCurrentDateTime
import com.example.gymlog.ui.theme.MyAppTheme

@Composable
fun ExerciseInWorkoutUI(
    onConfirmResult: (ResultOfSet) -> Unit,
    exerciseBlockUI: ExerciseBlockUI,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Exercise header and description
            ExerciseHeader(exerciseBlockUI)

            Spacer(Modifier.height(8.dp))

            // Subtle divider
            Divider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp
            )

            Spacer(Modifier.height(8.dp))

            // Results section - either current or historical
            DisplayResults(exerciseBlockUI.results)

            Spacer(Modifier.height(12.dp))

            // Action button for adding results - now more subtle
            ActionButton(
                resultsIsEmpty = exerciseBlockUI.results.isEmpty(),
                onClick = { showDialog = true }
            )
        }
    }

    // Dialog for entering results
    if(showDialog) {
        LogResultDialog(
            onDismiss = { showDialog = false },
            onConfirmResult = { result ->
                result.exeInBlockId = exerciseBlockUI.linkId
                onConfirmResult(result)
            }
        )
    }
}

@Composable
private fun ExerciseHeader(
    exerciseBlockUI: ExerciseBlockUI,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        // Exercise name - now more prominent
        Text(
            text = exerciseBlockUI.name,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )

        // Exercise description (if available)
        if (exerciseBlockUI.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = exerciseBlockUI.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DisplayResults(
    results: List<ResultOfSet>
) {
    if (results.isNotEmpty()) {
        Results(results)
    } else {
        EmptyResultsMessage()
    }
}

@Composable
private fun EmptyResultsMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_results),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Results(
    workoutResult: List<ResultOfSet>,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        workoutResult.forEachIndexed { index, result ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(6.dp))
            }

            ResultItem(result)
        }
    }
}

@Composable
private fun ResultItem(result: ResultOfSet) {
    val onesIterations = stringResource(R.string.ones_iterations)
    val onesWeight = stringResource(R.string.ones_weight)
    val onesSeconds = stringResource(R.string.ones_seconds)
    val setTxt = stringResource(R.string.set_txt)

    val repeatsText = result.iteration?.let { "$it $onesIterations" } ?: "-"
    val weightText = result.weight?.let { "$it $onesWeight" } ?: "–"
    val timeText = result.workTime?.let { "$it $onesSeconds" } ?: "-"

    // Check if date is today
    val showDate = result.currentDate == getCurrentDateTime().first

    val prefix = if (showDate) {
        "$setTxt ${result.currentDate}: "
    } else {
        "$setTxt: ${result.currentTime}: "
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 10.dp)
        ) {
            // Time prefix in lighter color
            Text(
                text = prefix,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Result details in a row
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ResultDetail(
                    value = repeatsText,
                    modifier = Modifier.weight(1f)
                )

                ResultDetail(
                    value = weightText,
                    modifier = Modifier.weight(1f)
                )

                ResultDetail(
                    value = timeText,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ResultDetail(
    value: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
private fun ActionButton(
    resultsIsEmpty: Boolean,
    onClick: () -> Unit
) {
    val buttonText = if (resultsIsEmpty) {
        stringResource(R.string.add_results)
    } else {
        stringResource(R.string.write_results)
    }

    // More subtle button that is still clearly visible
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Превью основного контенту екрану тренування
 */
@Preview(showBackground = true, name = "Превью екрану тренування")
@Composable
fun Preview_ExerciseInWorkoutUI() {
    MyAppTheme {
        // Приклад блоку тренування для превью
        ExerciseInWorkoutUI(
            onConfirmResult = {},
            ExerciseBlockUI(0,"Присідання", "Присідання зі штангою", emptyList())
        )
    }
}