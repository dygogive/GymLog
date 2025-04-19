package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.exercise.ExerciseInBlock
import com.example.gymlog.ui.theme.MyAppTheme


@Composable
fun ExerciseInWorkoutUI(
    exerciseInBlock : ExerciseInBlock,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer  // Виправлено на surface
) {

    Column(
        modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 16.dp)
    ){
        // Заголовок
        Text(
            text = exerciseInBlock.getNameOnly(LocalContext.current),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )  // Видалено зайві фігурні дужки {}
    }
}



// 2. Preview-функція, що юзає допоміжну
@Preview(
    showBackground = true,
    name = "ExerciseInWorkoutUI Preview"
)
@Composable
fun ExerciseInWorkoutUIPreview() {
    MyAppTheme {
        ExerciseInWorkoutUI(
            exerciseInBlock = ExerciseInBlock(
                101L, 1L,
                "Присідання зі штангою",
                "Класичні присідання",
                Motion.PRESS_BY_LEGS,
                listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
                Equipment.BARBELL,
                1
            )
        )
    }
}