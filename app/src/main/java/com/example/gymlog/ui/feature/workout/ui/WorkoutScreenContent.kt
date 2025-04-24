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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import com.example.gymlog.ui.feature.workout.model.TimerParams
import com.example.gymlog.ui.feature.workout.model.TrainingBlockInfo
import com.example.gymlog.ui.theme.MyAppTheme


@Composable
fun WorkoutScreenContent(
    timerParams: TimerParams,
    infoBlocks: List<TrainingBlockInfo>,
    onConfirmResult: (ResultOfSet) -> Unit,
    modifier: Modifier = Modifier
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }


    }
}


