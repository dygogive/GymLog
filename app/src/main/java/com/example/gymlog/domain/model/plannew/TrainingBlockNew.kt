package com.example.gymlog.domain.model.plannew

import com.example.gymlog.domain.model.attributenew.EquipmentNew
import com.example.gymlog.domain.model.attributenew.MotionNew
import com.example.gymlog.domain.model.attributenew.MuscleGroupNew
import com.example.gymlog.domain.model.exercisenew.ExerciseInBlockNew

data class TrainingBlockNew(
    val name: String,
    val description: String,
    val position: Int?,
    val exercises: List<ExerciseInBlockNew>,
    val motions: List<MotionNew>,
    val muscleGroups: List<MuscleGroupNew>,
    val equipment: List<EquipmentNew>
)