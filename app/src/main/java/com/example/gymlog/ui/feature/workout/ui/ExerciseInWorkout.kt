package com.example.gymlog.ui.feature.workout.ui

import android.util.Log
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.ui.theme.MyAppTheme


@Composable
fun ExerciseInWorkoutUI(
    exerciseInBlock : ExerciseInBlock,
    lastWorkoutExercises: List<WorkoutExercise>,
    currentWorkoutExercises: List<WorkoutExercise>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,  // Виправлено на surface
    onClickFixResults: () -> Unit
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
        ExerciseInBlockTexts(
            modifier = modifier.clickable { Log.d(TAG, "ExerciseInWorkoutUI: click") },
            exerciseInBlock = exerciseInBlock,
            lastWorkoutExercises = lastWorkoutExercises,
            currentWorkoutExercises = currentWorkoutExercises
        )

        Spacer(Modifier.height(8.dp))


        // --- Історія чи поточні результати ---
        if (currentWorkoutExercises.isNotEmpty()) {
            CurentResultList(currentWorkoutExercises)
        } else if (lastWorkoutExercises.isNotEmpty())  {
            LastResultList(lastWorkoutExercises)
        } else
            Text(
                text = stringResource(R.string.no_results),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        Spacer(Modifier.height(12.dp))


        val writeResults = stringResource(R.string.write_results)
        val addResults = stringResource(R.string.add_results)
        // --- Кнопка для запису нового результату ---
        Text(
            text = if (currentWorkoutExercises.isEmpty()) writeResults else addResults,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(onClick = onClickFixResults)
                .padding(vertical = 8.dp,horizontal = 8.dp)
        )
    }
}

@Composable
fun ExerciseInBlockTexts(
    modifier: Modifier = Modifier,
    exerciseInBlock: ExerciseInBlock,
    lastWorkoutExercises: List<WorkoutExercise>,
    currentWorkoutExercises: List<WorkoutExercise>,
){
    Column(
        modifier = modifier
    ) {
        // Заголовок
        Text(
            text = exerciseInBlock.getNameOnly(LocalContext.current),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )

        if (exerciseInBlock.description.isNotBlank()){
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
private fun CurentResultList(workExes: List<WorkoutExercise>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        if (workExes.isNotEmpty())
            workExes.forEach { workExe ->
            // Формуємо рядок: вага × повторення, тривалість, підхід №
                val setTxt = stringResource(R.string.set_txt)
                val onesIterations = stringResource(R.string.ones_iterations)
                val onesWeight = stringResource(R.string.ones_weight)
                val onesSeconds = stringResource(R.string.ones_seconds)
                val repeatsText = workExe.iteration.let { "$it $onesIterations" }
                val weightText = workExe.weight?.let { "$it $onesWeight" } ?: "–"
                val timeText = workExe.worktime?.let { "$it $onesSeconds" } ?: "-"
                Text(
                    text = "$setTxt ${workExe.orderInWorkSet}: $repeatsText $weightText $timeText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
    }
}


@Composable
private fun LastResultList(workExes: List<WorkoutExercise>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        if (workExes.isNotEmpty())
            workExes.forEach { workExe ->
                // Формуємо рядок: вага × повторення, тривалість, підхід №
                val setTxt = stringResource(R.string.set_txt)
                val onesIterations = stringResource(R.string.ones_iterations)
                val onesWeight = stringResource(R.string.ones_weight)
                val onesSeconds = stringResource(R.string.ones_seconds)
                val repeatsText = workExe.iteration.let { "$it $onesIterations" }
                val weightText = workExe.weight?.let { "$it $onesWeight" } ?: "–"
                val timeText = workExe.worktime?.let { "$it $onesSeconds" } ?: "-"
                Text(
                    text = "$setTxt ${workExe.date}: $repeatsText $weightText $timeText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
    }
}




// 2. Preview-функція, що юзає допоміжну
@Preview(
    showBackground = true,
    name = "ExerciseInWorkoutUI — історія"
)
@Composable
fun ExerciseInWorkoutHistoryPreview() {
    MyAppTheme {
        ExerciseInWorkoutUI(
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

            // Імітуємо 3 останні результати з різних днів
            lastWorkoutExercises = listOf(
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
            ),

            // Жодного результату в поточному тренуванні
            currentWorkoutExercises = emptyList(),

            onClickFixResults = {}
        )
    }
}
