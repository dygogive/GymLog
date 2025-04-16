package com.example.gymlog.presentation.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.domain.model.exercise.Equipment
import com.example.gymlog.domain.model.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.exercise.Motion
import com.example.gymlog.domain.model.exercise.MuscleGroup
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.presentation.theme.MyAppTheme

@Composable
fun TrainingBlockWorkout(
    block: TrainingBlock,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = block.name,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))

        // Опис
        if (block.description.isNotBlank()) {
            Text(
                text = block.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
        }

        // Список типів руху
        Text("Типи руху:", style = MaterialTheme.typography.bodyMedium)
        block.motions.forEach { motion ->
            Text("- ${motion.name}", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        // Список обладнання
        Text("Обладнання:", style = MaterialTheme.typography.bodyMedium)
        block.equipmentList.forEach { eq ->
            Text("- ${eq.name}", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        // Список м’язових груп
        Text("М’язові групи:", style = MaterialTheme.typography.bodyMedium)
        block.muscleGroupList.forEach { mg ->
            Text("- ${mg.name}", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        // Вправи в блоці
        block.exercises.forEach { ex ->
            Text(
                text = ex.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}



@Preview(
    showBackground = true,
    name = "TrainingBlockParameters Preview"
)
@Composable
fun TrainingBlockWorkoutPreview() {
    MyAppTheme {
        TrainingBlockWorkout(
            block = TrainingBlock(
                1L,
                1L,
                "Full Body Blast",
                "Комплекс на всі групи м'язів",
                listOf(Motion.PRESS_UPWARDS, Motion.PULL_DOWNWARDS),
                listOf(MuscleGroup.CHEST, MuscleGroup.LATS, MuscleGroup.QUADRICEPS),
                listOf(Equipment.BARBELL, Equipment.DUMBBELLS),
                0,
                listOf(
                    ExerciseInBlock(
                        101L,
                        1L,
                        "Присідання зі штангою",
                        "Класичні присідання",
                        Motion.PRESS_BY_LEGS,
                        listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
                        Equipment.BARBELL,
                        1
                    ),
                    ExerciseInBlock(
                        102L,
                        2L,
                        "Тяга в блоковому тренажері",
                        "Для м’язів спини",
                        Motion.PULL_DOWNWARDS,
                        listOf(MuscleGroup.LATS, MuscleGroup.LONGISSIMUS),
                        Equipment.CABLE_MACHINE,
                        2
                    ),
                    ExerciseInBlock(
                        103L,
                        3L,
                        "Жим лежачи",
                        "Груди та трицепси",
                        Motion.PRESS_MIDDLE,
                        listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                        Equipment.BARBELL,
                        3
                    )
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}