// WorkoutUiState.kt
package com.example.gymlog.presentation.state

import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.plan.FitnessProgram
import com.example.gymlog.domain.model.plan.GymDay
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.domain.model.workout.WorkoutResult
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * UI‑стан екрану Workout:
 * — загальні таймери
 * — список блоків
 * — вибір програми/дня
 * — накопичені результати підходів
 */
data class WorkoutUiState(
    // таймери
    val totalTimeMs: Long = 0L,
    val lastSetTimeMs: Long = 0L,
    val isGymRunning: Boolean = false,

    // тренувальні блоки
    val blocks: PersistentList<TrainingBlock> = persistentListOf(),

    // діалог вибору програми → дня
    val availablePrograms: PersistentList<FitnessProgram> = persistentListOf(),
    val selectedProgram: FitnessProgram? = null,
    val availableGymDaySessions: PersistentMap<Long, PersistentList<GymDay>> = persistentMapOf(),
    val selectedGymDay: GymDay? = null,
    val showSelectionDialog: Boolean = true,

    // вправи і їх результати
    val exercises: PersistentList<WorkoutExercise> = persistentListOf(),
    val results: PersistentMap<Long, PersistentList<WorkoutResult>> = persistentMapOf()
) {
    companion object {
        /** Дані для превʼю та прикладу */
        fun previewState() = WorkoutUiState(
            exercises = persistentListOf(
                WorkoutExercise(
                    id = 1,
                    workoutGymDayId = 10,
                    exerciseId = 101,
                    name = "Присідання зі штангою",
                    description = null,
                    motion = Motion.PRESS_BY_LEGS.name,
                    muscleGroups = listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES)
                        .joinToString(",") { it.name },
                    equipment = Equipment.BARBELL.name,
                    comments = "Very good!"
                )
            ),
            results = persistentMapOf(
                1L to persistentListOf(
                    WorkoutResult(
                        id = 11,
                        workoutExerciseId = 1,
                        weight = 80,
                        iteration = 8,
                        workTime = 60,
                        orderInWorkoutExercise = 1,
                        orderInWorkSet = 1,
                        orderInWorkGymDay = 1,
                        minutesSinceStartWorkout = 2,
                        date = "2025-04-23T12:15:00"
                    )
                )
            )
        )
    }
}
