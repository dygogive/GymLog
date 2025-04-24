package com.example.gymlog.ui.feature.workout.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.gymlog.ui.feature.workout.model.AttributesInfo

val TAG: String = "logtag"


@Composable
fun Attributes(
    attributesInfo: AttributesInfo,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val motions = attributesInfo.motion.motions
    val muscles = attributesInfo.muscle.muscles
    val equipments = attributesInfo.equipment.equipments

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (motions.isNotEmpty()) {
            AttributeSection(
                title = stringResource(R.string.motion),
                items = motions
            )
        }

        if (muscles.isNotEmpty()) {
            AttributeSection(
                title = stringResource(R.string.muscles),
                items = muscles
            )
        }

        if (equipments.isNotEmpty()) {
            AttributeSection(
                title = stringResource(R.string.equipment),
                items = equipments
            )
        }
    }
}








@Composable
private fun AttributeSection(
    title: String,
    items: List<String>
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
        items.forEach { item ->
            Text(
                text = "• $item",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

