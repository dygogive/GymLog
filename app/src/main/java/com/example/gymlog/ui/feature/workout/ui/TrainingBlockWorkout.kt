package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.ui.feature.workout.model.AttributesInfo
import com.example.gymlog.ui.feature.workout.model.EquipmentStateList
import com.example.gymlog.ui.feature.workout.model.ExerciseBlockUI
import com.example.gymlog.ui.feature.workout.model.MotionStateList
import com.example.gymlog.ui.feature.workout.model.MusclesStateList
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import com.example.gymlog.ui.feature.workout.model.TrainingBlockUiModel
import com.example.gymlog.ui.theme.MyAppTheme

@Composable
fun TrainingBlockWorkout(
    trainBlockInfo: TrainingBlockUiModel,
    onConfirmResult: (ResultOfSet) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Block header and description
            BlockHeader(trainBlockInfo)

            Spacer(Modifier.height(12.dp))

            // Subtle divider
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Attributes toggle button
            AttributesToggle(
                expanded = expanded,
                onClick = { expanded = !expanded }
            )

            // Animated attributes section
            AnimatedVisibility(visible = expanded) {
                Attributes(
                    attributesInfo = trainBlockInfo.attributesInfo,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Exercises in block
            trainBlockInfo.infoExercises.forEach { exeInfo ->
                ExerciseInWorkoutUI(
                    onConfirmResult = onConfirmResult,
                    exerciseBlockUI = exeInfo,
                    backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun BlockHeader(
    trainBlockInfo: TrainingBlockUiModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Title
        Text(
            text = trainBlockInfo.name,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        // Description (if available)
        if (trainBlockInfo.description.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = trainBlockInfo.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AttributesToggle(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (expanded)
                    stringResource(R.string.collapse_attributes)
                else
                    stringResource(R.string.show_attributes),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
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
fun Preview_ScreenContent() {
    MyAppTheme {
        // Приклад блоку тренування для превью
        val sampleBlock = TrainingBlockUiModel(
            name = "День ніг",
            description = "Квадрицепси, Біцепси стегна",
            attributesInfo = AttributesInfo(
                motionStateList = MotionStateList(listOf("Жим", "Тяга")),
                muscleStateList = MusclesStateList(listOf("Квадрицепси", "Біцепс стегна")),
                equipmentStateList = EquipmentStateList(listOf("Штанга", "Стійка"))
            ),
            infoExercises = listOf(
                ExerciseBlockUI(0,"Присідання", "Присідання зі штангою", emptyList()),
                ExerciseBlockUI(0,"Випади", "Випади з кроком", emptyList())
            )
        )

        // Приклад блоку тренування для превью
        TrainingBlockWorkout(
            sampleBlock,
            onConfirmResult = {}
        )
    }
}