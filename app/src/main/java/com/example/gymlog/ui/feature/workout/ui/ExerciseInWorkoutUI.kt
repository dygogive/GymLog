package com.example.gymlog.ui.feature.workout.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    exerciseInBlockUI: ExerciseBlockUI,
    expandedExeId: Long,
    onClickExpandExercise: (Long)->Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
) {
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

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
            // Exercise header with toggle button to the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Exercise name only (no description yet)
                ExerciseNameOnly(
                    exerciseBlockUI = exerciseInBlockUI,
                    modifier = Modifier.weight(1f)
                )

                Log.d("ExpandToggleButton", "ExpandToggleButton: 0 - $expandedExeId")

                // Toggle button for expanding/collapsing details
                ExpandToggleButton(
                    expanded = (exerciseInBlockUI.linkId == expandedExeId),
                    onClick = {onClickExpandExercise(exerciseInBlockUI.linkId)}
                )
            }

            // Animated visibility for description and results
            AnimatedVisibility(visible = (exerciseInBlockUI.linkId == expandedExeId)) {
                Column {
                    // Show description if available
                    if (exerciseInBlockUI.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        ExerciseDescription(exerciseInBlockUI.description)
                    }

                    Spacer(Modifier.height(8.dp))

                    // Subtle divider
                    Divider(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        thickness = 1.dp
                    )

                    Spacer(Modifier.height(8.dp))

                    // Results section - either current or historical
                    DisplayResults(exerciseInBlockUI.results)

                    // Position the add button after results when expanded
                    Spacer(Modifier.height(12.dp))

                    // Add result button appears below results
                    AddResultButton(
                        resultsIsEmpty = exerciseInBlockUI.results.isEmpty(),
                        onClick = { showDialog = true }
                    )
                }
            }
        }
    }

    // Dialog for entering results
    if(showDialog) {
        LogResultDialog(
            onDismiss = { showDialog = false },
            onConfirmResult = { result ->
                result.exeInBlockId = exerciseInBlockUI.linkId
                onConfirmResult(result)
            }
        )
    }
}

@Composable
private fun ExerciseNameOnly(
    exerciseBlockUI: ExerciseBlockUI,
    modifier: Modifier = Modifier,
) {
    Text(
        text = exerciseBlockUI.name,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
        modifier = modifier
    )
}

@Composable
private fun ExerciseDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun ExpandToggleButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("ExpandToggleButton", "ExpandToggleButton: 1 - $expanded")
    IconButton(
        onClick = onClick,
        modifier = modifier.size(36.dp)
    ) {
        Icon(
            imageVector = if (expanded)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown,
            contentDescription = if (expanded)
                stringResource(R.string.collapse_details)
            else
                stringResource(R.string.expand_details),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AddResultButton(
    resultsIsEmpty: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonText = if (resultsIsEmpty) {
        stringResource(R.string.add_results)
    } else {
        stringResource(R.string.write_results)
    }

    // Use Row to position the button at the end (right)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        // Floating action button style for adding results
        Button(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
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

    val prefix = if (!showDate) {
        "$setTxt ${result.currentDate}: "
    } else {
        "$setTxt ${result.currentTime}: "
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


/**
 * Превью основного контенту екрану тренування
 */
@Preview(showBackground = true, name = "Превью екрану тренування")
@Composable
fun Preview_ExerciseInWorkoutUI() {

    val result1 = ResultOfSet(
        0,
      0,
        200,
        0,
        0,
        "05.02.2025",
        "00:00",
    )
    val result2 = ResultOfSet(
        0,
        0,
        200,
        0,
        0,
        "02.02.2025",
        "00:00",
    )
    val result3 = ResultOfSet(
        0,
        0,
        250,
        0,
        0,
        "03.02.2025",
        "00:00",
    )
    val result4 = ResultOfSet(
        0,
        0,
        200,
        0,
        0,
        "04.02.2025",
        "00:00",
    )


    MyAppTheme {
        // Приклад блоку тренування для превью
        ExerciseInWorkoutUI(
            onConfirmResult = {},
            ExerciseBlockUI(
                0,
                "Присідання",
                "Присідання зі штангою",
                listOf(result1,result2,result3,result4)),
            expandedExeId = 0,
            { }
        )
    }
}