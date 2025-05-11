package com.example.gymlog.ui.feature.workout.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.core.utils.getCurrentDateTime
import com.example.gymlog.ui.feature.workout.model.ResultOfSet

/**
 * Components for displaying and managing exercise results
 */

@Composable
fun ResultItem(
    result: ResultOfSet,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
            }
        }
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

