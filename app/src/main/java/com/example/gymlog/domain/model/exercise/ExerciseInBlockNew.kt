package com.example.gymlog.domain.model.exercise

import com.example.gymlog.domain.model.attribute.equipment.EquipmentNew
import com.example.gymlog.domain.model.attribute.motion.MotionNew
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroupNew

data class ExerciseInBlockNew (
    val description:    String,
    val motion:         MotionNew,
    val muscleGroups:   List<MuscleGroupNew>,
    val equipment:      EquipmentNew,
    val position:       Int
)