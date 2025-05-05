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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
                    text = trainBlockInfo.name,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )

                // Опис
                if (trainBlockInfo.description.isNotBlank()) {
                    Text(
                        text = trainBlockInfo.description,
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
            //
            text = if (expanded)
                stringResource(R.string.collapse_attributes)
            else stringResource(R.string.show_attributes),
            //
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp)
        )

        // Анімований блок атрибутів
        AnimatedVisibility(visible = expanded) {
            Attributes(
                attributesInfo = trainBlockInfo.attributesInfo,
                modifier = Modifier.fillMaxWidth()
            )
        }



        Spacer(Modifier.height(8.dp))



        // Вправи в блоці
        trainBlockInfo.infoExercises.forEach { exeInfo ->
            ExerciseInWorkoutUI(
                onConfirmResult = onConfirmResult,
                exerciseBlockUI = exeInfo,
            )
            Spacer(modifier = Modifier.height(8.dp))
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




