package com.example.gymlog.presentation.state

import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.plan.FitnessProgram
import com.example.gymlog.domain.model.plan.GymDay
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.domain.model.workout.WorkoutExercise
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf


data class WorkoutUiState(
    val totalTimeMs: Long = 0L,
    var lastSetTimeMs: Long = 0L,
    val blocks: PersistentList<TrainingBlock> = persistentListOf<TrainingBlock>(),
    val isGymRunning: Boolean = false,      // Чи активне тренування


    //для діалогу вибору
    val availablePrograms: PersistentList<FitnessProgram> = persistentListOf(),
    val selectedProgram: FitnessProgram? = null,
    val availableGymDaySessions: PersistentMap<Long, List<GymDay>> = persistentMapOf(),
    val selectedGymDay: GymDay? = null,

    // нове: чи показувати діалог
    val showSelectionDialog: Boolean = true,

    //результати для вправ
    val currentResults: List<WorkoutExercise> = emptyList(),
    val historyResults: List<WorkoutExercise> = emptyList(),
) {

    fun lastWorkoutExercises(): List<WorkoutExercise> {
        return listOf(WorkoutExercise(
            id = 11L,
            workoutGymDayId = 20L,
            exerciseId = 101L,
            name = "Присідання зі штангою",
            description = null,
            motion = Motion.PRESS_BY_LEGS.name,
            muscleGroups = MuscleGroup.QUADRICEPS.name,
            equipment = Equipment.BARBELL.name,
            comments = "Very good!"
        ))
    }

    fun currentWorkoutExercises(): List<WorkoutExercise> {
        return listOf(WorkoutExercise(
            id = 1L,
            workoutGymDayId = 10L,
            exerciseId = 101L,
            name = "Присідання зі штангою",
            description = null,
            motion = Motion.PRESS_BY_LEGS.name,
            muscleGroups = listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES)
                .joinToString(",") { it.name },
            equipment = Equipment.BARBELL.name,
            comments = "Very good!"
        ))
    }

}