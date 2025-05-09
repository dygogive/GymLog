package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
    expandedExeId: Long,
    onClickExpandExercise: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),   // Match ExerciseInWorkoutUI corner radius
        shadowElevation = 2.dp               // Match ExerciseInWorkoutUI elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)              // Match ExerciseInWorkoutUI padding
        ) {
            // Block header with attribute toggle button to the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Block header and description
                BlockHeader(
                    trainBlockInfo = trainBlockInfo,
                    modifier = Modifier.weight(1f)
                )

                // Small attribute toggle button with icon
                AttributesToggle(
                    expanded = expanded,
                    onClick = { expanded = !expanded }
                )
            }

            Spacer(Modifier.height(8.dp))    // Match ExerciseInWorkoutUI spacing

            // Subtle divider
            Divider(
                color = MaterialTheme.colorScheme.surfaceVariant,  // Match ExerciseInWorkoutUI divider
                thickness = 1.dp                                   // Match ExerciseInWorkoutUI thickness
            )

            Spacer(Modifier.height(8.dp))    // Match ExerciseInWorkoutUI spacing

            // Animated attributes section
            AnimatedVisibility(visible = expanded) {
                Attributes(
                    attributesInfo = trainBlockInfo.attributesInfo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))   // Match ExerciseInWorkoutUI spacing

            // Exercises in block
            trainBlockInfo.infoExercises.forEach { exerciseUImodel ->
                ExerciseInWorkoutUI(
                    onConfirmResult = onConfirmResult,
                    exerciseInBlockUI = exerciseUImodel,
                    expandedExeId = expandedExeId,
                    onClickExpandExercise = onClickExpandExercise,
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
            color = MaterialTheme.colorScheme.onSurface,       // Match ExerciseInWorkoutUI text color
            style = MaterialTheme.typography.bodyLarge.copy( // Match ExerciseInWorkoutUI text style
                fontWeight = FontWeight.Medium                 // Match ExerciseInWorkoutUI weight
            )
        )

        // Description (if available)
        if (trainBlockInfo.description.isNotBlank()) {
            Text(
                text = trainBlockInfo.description,
                style = MaterialTheme.typography.bodyMedium,       // Match ExerciseInWorkoutUI text style
                color = MaterialTheme.colorScheme.onSurfaceVariant // Match ExerciseInWorkoutUI text color
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
    IconButton(
        onClick = onClick,
        modifier = modifier.size(36.dp)
    ) {
        Icon(
            imageVector = if (expanded)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.Info,
            contentDescription = if (expanded)
                stringResource(R.string.collapse_attributes)
            else
                stringResource(R.string.show_attributes),
            tint = MaterialTheme.colorScheme.primary
        )
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
            onConfirmResult = {},
            0,
            {}
        )
    }
}