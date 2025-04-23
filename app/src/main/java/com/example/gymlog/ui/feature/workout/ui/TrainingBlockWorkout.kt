package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.ui.theme.MyAppTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.gymlog.R
import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.ui.feature.workout.model.ResultOfSet

@Composable
fun TrainingBlockWorkout(
    results: List<ResultOfSet>,
    exerciseInfo
    onSaveResult: (exerciseId: Long, iterations: Int, weight: Float?, seconds: Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp, horizontal = 8.dp),
    ) {


        Row(
            modifier = Modifier  // <-- Використовуємо переданий модифікатор
                .padding(vertical = 0.dp, horizontal = 0.dp)
        ) {
            Column(
                modifier = Modifier  // <-- Використовуємо переданий модифікатор
                    .fillMaxWidth()
            ) {
                // Заголовок
                Text(
                    text = trainBlock.name,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )

                // Опис
                if (trainBlock.description.isNotBlank()) {
                    Text(
                        text = trainBlock.description,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }


        Spacer(Modifier.height(8.dp))



        // Кнопка чи текст
        Text(
            text = if (expanded)
                stringResource(R.string.collapse_attributes)
            else stringResource(R.string.show_attributes),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp)
        )

        // Анімований блок атрибутів
        AnimatedVisibility(visible = expanded) {
            Attributes(
                trainingBlock = trainBlock,
                modifier = Modifier.fillMaxWidth()
            )
        }



        Spacer(Modifier.height(8.dp))



        // Вправи в блоці
        trainBlock.exercises.forEach { exerciseInBlock ->
            ExerciseInWorkoutUI(
                results = results,
                onConfirmResult = onConfirmResult,
                exerciseInfo = exerciseInfo,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}












