package com.example.gymlog.domain.model.exercisenew

import com.example.gymlog.domain.model.attribute.equipment.EquipmentNew
import com.example.gymlog.domain.model.attribute.motion.MotionNew
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroupNew

open class ExerciseNew (
    val name:           String,
    val description:    String,
    val motion: MotionNew,
    val muscleGroups:   List<MuscleGroupNew>,
    val equipment: EquipmentNew,
)