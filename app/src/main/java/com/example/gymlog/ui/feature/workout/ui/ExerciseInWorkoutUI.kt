package com.example.gymlog.ui.feature.workout.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymlog.R
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.presentation.viewmodel.WorkoutViewModel
import com.example.gymlog.ui.theme.MyAppTheme
import androidx.hilt.navigation.compose.hiltViewModel



@Composable
fun ExerciseInWorkoutUI(
    exerciseInBlock: ExerciseInBlock,
    workoutViewModel: WorkoutViewModel = hiltViewModel()
) {

    val state by workoutViewModel.uiState.collectAsState()
    val currentResults = state.currentWorkoutExercises()
    val lastResults = state.lastWorkoutExercises()


    ExerciseInWorkoutContent(
        exerciseInBlock,
        onSaveResult = workoutViewModel::saveResult,
        currentResults,
        lastResults
    )



}










@Composable
fun ExerciseInWorkoutContent(
    exerciseInBlock: ExerciseInBlock,
    onSaveResult: (exerciseId: Long, iterations: Int, weight: Float?, seconds: Int?) -> Unit,
    currentResults: List<WorkoutExercise>,
    lastResults: List<WorkoutExercise>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
) {

    //first dialog don't show
    var showDialog by remember { mutableStateOf(false) }




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
    ) {
        // Exercise header and description
        ExerciseHeader(
            exerciseInBlock = exerciseInBlock
        )

        Spacer(Modifier.height(8.dp))

        // Results section - either current or historical
        DisplayResults(
            currentWorkoutExercises = currentResults,
            lastWorkoutExercises = lastResults
        )

        Spacer(Modifier.height(12.dp))

        // Action button for adding results
        ActionButton(
            hasCurrentResults = currentResults.isNotEmpty(),
            onClick = {
                showDialog = true
            }
        )
    }


    //показати діалог введення результатів

    if(showDialog) {
        LogResultDialog(
            onDismiss = {showDialog = false},
            //
            onConfirm = { iterations, weight, seconds ->
                //
                onSaveResult(
                    exerciseInBlock.id,
                    iterations,
                    weight,
                    seconds)

                Log.d("tag1",  "test2" )
                //
                showDialog = false
                //
            }
        )
    }

}

@Composable
private fun ExerciseHeader(
    exerciseInBlock: ExerciseInBlock,
    modifier: Modifier = Modifier,
) {
    val context: Context = LocalContext.current

    Column(modifier = modifier.clickable {
        Toast.makeText(context, "ExerciseHeader: click", Toast.LENGTH_LONG).show()
    }) {
        // Exercise name
        Text(
            text = exerciseInBlock.getNameOnly(LocalContext.current),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Exercise description (if available)
        if (exerciseInBlock.description.isNotBlank()) {
            Text(
                text = exerciseInBlock.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun DisplayResults(
    currentWorkoutExercises: List<WorkoutExercise>,
    lastWorkoutExercises: List<WorkoutExercise>
) {
    when {
        currentWorkoutExercises.isNotEmpty() -> {
            ResultList(
                workoutExercises = currentWorkoutExercises,
                showDate = false
            )
        }
        lastWorkoutExercises.isNotEmpty() -> {
            ResultList(
                workoutExercises = lastWorkoutExercises,
                showDate = true
            )
        }
        else -> {
            Text(
                text = stringResource(R.string.no_results),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun ResultList(
    workoutExercises: List<WorkoutExercise>,
    showDate: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        workoutExercises.forEach { exercise ->
            val onesIterations = stringResource(R.string.ones_iterations)
            val onesWeight = stringResource(R.string.ones_weight)
            val onesSeconds = stringResource(R.string.ones_seconds)
            val setTxt = stringResource(R.string.set_txt)

            val repeatsText = "${exercise.iteration} $onesIterations"
            val weightText = exercise.weight?.let { "$it $onesWeight" } ?: "–"
            val timeText = exercise.worktime?.let { "$it $onesSeconds" } ?: "-"

            val prefix = if (showDate) {
                "$setTxt ${exercise.date}"
            } else {
                "$setTxt ${exercise.orderInWorkSet}"
            }

            Text(
                text = "$prefix: $repeatsText $weightText $timeText",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun ActionButton(
    hasCurrentResults: Boolean,
    onClick: () -> Unit
) {

    val buttonText = if (hasCurrentResults) {
        stringResource(R.string.add_results)
    } else {
        stringResource(R.string.write_results)
    }

    Text(
        text = buttonText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable(onClick = {
                onClick()
            })
            .padding(vertical = 8.dp, horizontal = 8.dp)
    )
}

@Preview(
    showBackground = true,
    name = "ExerciseInWorkoutUI — історія"
)
@Composable
fun ExerciseInWorkoutUIPreview() {
    MyAppTheme {
        ExerciseInWorkoutContent(
            exerciseInBlock = ExerciseInBlock(
                101L,
                1L,
                "Присідання зі штангою",
                "Класичні присідання",
                Motion.PRESS_BY_LEGS,
                listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
                Equipment.BARBELL,
                1
            ),
            onSaveResult = { _, _, _, _ -> },
            currentResults = createSampleHistoricalWorkouts(),
            lastResults = emptyList(),
        )
    }
}

private fun createSampleHistoricalWorkouts(): List<WorkoutExercise> {
    return listOf(
        WorkoutExercise(
            id = 1L,
            workoutGymDayId = 10L,
            exerciseId = 101L,
            name = "Присідання зі штангою",
            description = null,
            motion = Motion.PRESS_BY_LEGS.name,
            muscleGroups = MuscleGroup.QUADRICEPS.name,
            equipment = Equipment.BARBELL.name,
            weight = 20,
            iteration = 10,
            worktime = 30,
            orderInWorkSet = 1,
            orderInWorkGymDay = 1,
            minutesSinceStartWorkout = 5,
            date = "20.04.2025"
        ),
        WorkoutExercise(
            id = 2L,
            workoutGymDayId = 9L,
            exerciseId = 101L,
            name = "Присідання зі штангою",
            description = null,
            motion = Motion.PRESS_BY_LEGS.name,
            muscleGroups = MuscleGroup.QUADRICEPS.name,
            equipment = Equipment.BARBELL.name,
            weight = 18,
            iteration = 8,
            worktime = 28,
            orderInWorkSet = 1,
            orderInWorkGymDay = 2,
            minutesSinceStartWorkout = 7,
            date = "18.04.2025"
        ),
        WorkoutExercise(
            id = 3L,
            workoutGymDayId = 8L,
            exerciseId = 101L,
            name = "Присідання зі штангою",
            description = null,
            motion = Motion.PRESS_BY_LEGS.name,
            muscleGroups = MuscleGroup.QUADRICEPS.name,
            equipment = Equipment.BARBELL.name,
            weight = 22,
            iteration = 6,
            worktime = 32,
            orderInWorkSet = 1,
            orderInWorkGymDay = 3,
            minutesSinceStartWorkout = 10,
            date = "16.04.2025"
        )
    )
}