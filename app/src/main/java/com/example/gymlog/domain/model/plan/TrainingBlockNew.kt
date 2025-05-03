package com.example.gymlog.domain.model.plan

import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew
import com.example.gymlog.domain.model.exercise.ExerciseInBlockNew

data class TrainingBlockNew(
    val id: Long = 0,
    val name: String,
    val description: String,
    val position: Int?,
    val exercises: List<ExerciseInBlockNew>,
    val motions: List<MotionNew>,
    val muscleGroups: List<MuscleGroupNew>,
    val equipment: List<EquipmentNew>
)