package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.ui.feature.workout.model.AttributesInfo

val TAG: String = "logtag"


@Composable
fun Attributes(
    attributesInfo: AttributesInfo,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val motions = attributesInfo.motionStateList.motions
    val muscles = attributesInfo.muscleStateList.muscles
    val equipments = attributesInfo.equipmentStateList.equipments

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




