package com.example.gymlog.domain.model.exercise

import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew


class ExerciseInBlockNew (
    val linkId: Long = 0,
    val exerciseId: Long = 0,
    name: String,
    description: String,
    motion: MotionNew,
    muscleGroups: List<MuscleGroupNew>,
    equipment: EquipmentNew,
    isCustom: Boolean,
    val position: Int,
    val workoutResults: List<WorkoutResult> = emptyList()
): ExerciseNew(
    exerciseId,
    name,
    description,
    motion,
    muscleGroups,
    equipment,
    isCustom
)