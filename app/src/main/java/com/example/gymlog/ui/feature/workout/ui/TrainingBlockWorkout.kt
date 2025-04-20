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
import androidx.compose.ui.graphics.Color
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

@Composable
fun TrainingBlockWorkout(
    trainBlock: TrainingBlock,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,  // <- дефолт
    onClickFixResults: () -> Unit
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
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 0.dp, horizontal = 0.dp)
        ) {
            Column(
                modifier = Modifier  // <-- Використовуємо переданий модифікатор
                    .fillMaxWidth()
                    .background(backgroundColor)
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
        trainBlock.exercises.forEach { ex ->
            ExerciseInWorkoutUI(
                ex,
                onClickFixResults = onClickFixResults)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


// 1. Допоміжна функція для прев’ю
fun createPreviewTrainingBlock(): TrainingBlock {
    return TrainingBlock(
        1L,                           // id
        1L,                           // gymDayId
        "Full Body Blast",            // name
        "Комплекс на всі групи м'язів", // description
        listOf(Motion.PRESS_UPWARDS, Motion.PULL_DOWNWARDS),          // motions
        listOf(MuscleGroup.CHEST, MuscleGroup.LATS, MuscleGroup.QUADRICEPS), // muscleGroups
        listOf(Equipment.BARBELL, Equipment.DUMBBELLS),               // equipmentList
        0,                            // position
        listOf(                        // exerciseInBlocks
            ExerciseInBlock(
                101L, 1L,
                "Присідання зі штангою",
                "Класичні присідання",
                Motion.PRESS_BY_LEGS,
                listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
                Equipment.BARBELL,
                1
            ),
            ExerciseInBlock(
                102L, 2L,
                "Тяга в блоковому тренажері",
                "Для м’язів спини",
                Motion.PULL_DOWNWARDS,
                listOf(MuscleGroup.LATS, MuscleGroup.LONGISSIMUS),
                Equipment.CABLE_MACHINE,
                2
            ),
            ExerciseInBlock(
                103L, 3L,
                "Жим лежачи",
                "Груди та трицепси",
                Motion.PRESS_MIDDLE,
                listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                Equipment.BARBELL,
                3
            )
        )
    )
}

// 2. Preview-функція, що юзає допоміжну
@Preview(
    showBackground = true,
    name = "TrainingBlockParameters Preview"
)
@Composable
fun TrainingBlockWorkoutPreview() {
    MyAppTheme {
        TrainingBlockWorkout(
            trainBlock = createPreviewTrainingBlock(),
            modifier = Modifier.padding(16.dp),
            onClickFixResults = { }
        )
    }
}