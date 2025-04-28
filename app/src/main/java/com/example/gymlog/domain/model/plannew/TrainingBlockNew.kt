package com.example.gymlog.domain.model.plannew

import com.example.gymlog.domain.model.attribute.equipment.EquipmentNew
import com.example.gymlog.domain.model.attribute.motion.MotionNew
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroupNew
import com.example.gymlog.domain.model.exercisenew.ExerciseInBlockNew

data class TrainingBlockNew(
    val name: String,
    val description: String,
    val position: Int,
    val exercises: List<ExerciseInBlockNew>,
    val motions: List<MotionNew>,
    val muscleGroups: List<MuscleGroupNew>,
    val equipment: List<EquipmentNew>
)