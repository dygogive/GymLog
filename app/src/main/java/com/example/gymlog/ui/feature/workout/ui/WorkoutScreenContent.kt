package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.ui.feature.workout.model.AttributesInfo
import com.example.gymlog.ui.feature.workout.model.EquipmentStateList
import com.example.gymlog.ui.feature.workout.model.ExerciseBlockUI
import com.example.gymlog.ui.feature.workout.model.MotionStateList
import com.example.gymlog.ui.feature.workout.model.MusclesStateList
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import com.example.gymlog.ui.feature.workout.model.TimerParams
import com.example.gymlog.ui.feature.workout.model.TrainingBlockUiModel
import com.example.gymlog.ui.theme.MyAppTheme


@Composable
fun WorkoutScreenContent(
    timerParams: TimerParams,
    infoBlocks: List<TrainingBlockUiModel>,
    onConfirmResult: (ResultOfSet) -> Unit,
    expandedExeId: Long,
    onClickExpandExercise: (Long) -> Unit,
    onDeleteResult: (ResultOfSet) -> Unit,
    onEditResult: (ResultOfSet) -> Unit,
    modifier: Modifier = Modifier,

) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        TimerSection(
            timerParams = timerParams,
            modifier = Modifier
                .fillMaxWidth()  // Змініть fillMaxSize на fillMaxWidth
                .padding(16.dp)  // Тепер horizontal padding буде працювати як очікується
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(infoBlocks) {infoBlock ->
                TrainingBlockWorkout(
                    trainBlockInfo = infoBlock,
                    onConfirmResult,
                    expandedExeId = expandedExeId,
                    onClickExpandExercise = onClickExpandExercise,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onDeleteResult = onDeleteResult,
                    onEditResult = onEditResult,
                )
            }
        }


    }
}


/**
 * Превью основного контенту екрану тренування
 */
@Preview(showBackground = true, name = "Превью екрану тренування")
@Composable
fun Preview_WorkoutScreenContent() {
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

        WorkoutScreenContent(
            timerParams = TimerParams(0L, 0L, "Почати", {}, {}, false),
            infoBlocks = listOf(sampleBlock),
            onConfirmResult = {},
            0,
            {},
            {},
            {},
        )
    }
}