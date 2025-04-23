package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.ui.feature.workout.model.ExerciseInfo
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import com.example.gymlog.ui.feature.workout.model.getCurrentDateTime


@Composable
fun ExerciseInWorkoutUI(
    results: List<ResultOfSet>,
    onConfirmResult: (ResultOfSet) -> Unit,
    exerciseInfo: ExerciseInfo,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
) {
    //first dialog don't show
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 16.dp)
    ) {


        // Exercise header and description
        ExerciseHeader(
            exerciseInfo
        )

        Spacer(Modifier.height(8.dp))

        // Results section - either current or historical
        DisplayResults(
            results
        )

        Spacer(Modifier.height(12.dp))

        // Action button for adding results
        ActionButton(
            results.isEmpty(),
            onClick = {
                showDialog = true
            }
        )
    }


    //показати діалог введення результатів

    if(showDialog) {
        LogResultDialog(
            onDismiss = { showDialog = false },
            {}
        )
    }

}


//OK
@Composable
private fun ExerciseHeader(
    exerciseInfo: ExerciseInfo,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
    ) {
        // Exercise name
        Text(
            text = exerciseInfo.name,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Exercise description (if available)
        if (exerciseInfo.description.isNotBlank()) {
            Text(
                text = exerciseInfo.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}


//OK
@Composable
private fun DisplayResults(
    results: List<ResultOfSet>
) {
    when {
        results.isNotEmpty() -> {
            Results(
                results,
            )
        } else -> {
            Text(
                text = stringResource(R.string.no_results),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}


//OK
@Composable
private fun Results(
    workoutResult: List<ResultOfSet>,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        workoutResult.forEach { workoutResult ->
            val onesIterations = stringResource(R.string.ones_iterations)
            val onesWeight = stringResource(R.string.ones_weight)
            val onesSeconds = stringResource(R.string.ones_seconds)
            val setTxt = stringResource(R.string.set_txt)

            val repeatsText = workoutResult.iteration?.let { "$it $onesIterations" } ?: "-"
            val weightText = workoutResult.weight?.let { "$it $onesWeight" } ?: "–"
            val timeText = workoutResult.workTime?.let { "$it $onesSeconds" } ?: "-"

            //чи дата сьогоднішня?
            val showDate = workoutResult.currentDate == getCurrentDateTime().first

            val prefix = if (showDate) {
                "$setTxt ${workoutResult.currentDate}: "
            } else {
                "$setTxt: ${workoutResult.currentTime}: "
            }

            Text(
                text = "$prefix: $repeatsText $weightText $timeText",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}



//Ok
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

    Text(
        text = buttonText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable(onClick = {
                onClick()
            })
            .padding(vertical = 8.dp, horizontal = 8.dp)
    )
}




