package com.example.gymlog.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.feature.theme.MyAppTheme
import androidx.compose.ui.platform.LocalContext

@Composable
fun TrainingBlockWorkout(
    block: TrainingBlock,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface  // <- дефолт
) {
    val context = LocalContext.current



    Column(
        modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
    ) {
        // Заголовок
        Text(
            text = block.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))

        // Опис
        if (block.description.isNotBlank()) {
            Text(
                text = block.description,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
        }

        // Список типів руху
        Text("Типи руху:",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium)
        block.motions.forEach { motion ->
            Text("- ${motion.getDescription(context)}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        // Список обладнання
        Text("Обладнання:",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium)
        block.equipmentList.forEach { eq ->
            Text("- ${eq.getDescription(context)}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        // Список м’язових груп
        Text("М’язові групи:",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium)
        block.muscleGroupList.forEach { mg ->
            Text("- ${mg.getDescription(context)}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        // Вправи в блоці
        block.exercises.forEach { ex ->
            Text(
                text = ex.getNameOnly(context),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge
            )
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
            block = createPreviewTrainingBlock(),
            modifier = Modifier.padding(16.dp)
        )
    }
}