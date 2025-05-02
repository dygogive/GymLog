package com.example.gymlog.domain.model.exercise

import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew


open class ExerciseNew (
    val name:           String,
    val description:    String,
    val motion: MotionNew,
    val muscleGroups:   List<MuscleGroupNew>,
    val equipment: EquipmentNew,
    val isCustom: Boolean
)