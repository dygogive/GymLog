package com.example.gymlog.ui.feature.workout.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.domain.model.plan.TrainingBlock

val TAG: String = "logtag"


@Composable
fun Attributes(
    trainingBlock: TrainingBlock,
    modifier: Modifier = Modifier
){
    val context: Context = LocalContext.current


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        // motions
        if (trainingBlock.motions.isNotEmpty()) {
//            Log.d(TAG, "Attributes motions: ${trainingBlock.motions.isNotEmpty()}; ${trainingBlock.motions.size}")

            Text(
                stringResource(R.string.motion),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            trainingBlock.motions.forEach { motion ->
                Text(
                    "- ${motion.getDescription(context)}",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // equipmentList
        if (trainingBlock.equipmentList.isNotEmpty()) {
//            Log.d(TAG, "Attributes motions: ${trainingBlock.motions.isNotEmpty()}; ${trainingBlock.motions.size}")

            Text(
                stringResource(R.string.equipment),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            trainingBlock.equipmentList.forEach { eq ->
                Text(
                    "- ${eq.getDescription(context)}",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // muscleGroupList
        if (trainingBlock.muscleGroupList.isNotEmpty()) {
            Text(
                stringResource(R.string.muscles),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            trainingBlock.muscleGroupList.forEach { mg ->
                Text(
                    "- ${mg.getDescription(context)}",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}