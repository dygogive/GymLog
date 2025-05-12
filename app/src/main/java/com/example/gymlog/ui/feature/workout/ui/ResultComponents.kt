package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.core.utils.getCurrentDateTime
import com.example.gymlog.ui.feature.workout.model.ResultOfSet

/**
 * Components for displaying and managing exercise results
 */



@Composable
fun DisplayResults(
    results: List<ResultOfSet>,
    onDeleteResult: (ResultOfSet) -> Unit,
    onEditResult: (ResultOfSet) -> Unit,
) {
    if (results.isNotEmpty()) {
        Results(
            results,
            onDeleteResult = onDeleteResult,
            onEditResult = onEditResult,
        )
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
    onDeleteResult: (ResultOfSet) -> Unit,
    onEditResult: (ResultOfSet) -> Unit,
) {




    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        workoutResult.forEachIndexed { index, result ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(6.dp))
            }

            ResultItem(
                result,
                onEditResult = onEditResult,
                onDeleteClick = {
                    onDeleteResult(result)
                }
            )


//            ResultItem(
//                result,
//                onEditClick = {
//                    showDialogEditResult = true
//                },
//                onDeleteClick = {
//                    onDeleteResult(result)
//                }
//            )



        }


    }
}










@Composable
fun ResultItem(
    result: ResultOfSet,
    onEditResult: (ResultOfSet) -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDialogEditResult by remember { mutableStateOf(false) }



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
        "$setTxt ${result.currentDate}"
    } else {
        "$setTxt ${result.currentTime}"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
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

                EditDeleteButtonMenu(
                    onEditClick = {showDialogEditResult = true},
                    onDeleteClick = onDeleteClick
                )
            }
        }
    }


    // Dialog for editing results
    if (showDialogEditResult) {
        LogResultDialog(
            result = result,
            onDismiss = { showDialogEditResult = false },
            onConfirmResult = onEditResult,
        )
    }
}

@Composable
fun ResultDetail(
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

