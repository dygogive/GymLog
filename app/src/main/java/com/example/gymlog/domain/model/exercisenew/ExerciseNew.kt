package com.example.gymlog.domain.model.exercisenew

import com.example.gymlog.domain.model.attributenew.EquipmentNew
import com.example.gymlog.domain.model.attributenew.MotionNew
import com.example.gymlog.domain.model.attributenew.MuscleGroupNew


open class ExerciseNew (
    val name:           String,
    val description:    String,
    val motion: MotionNew,
    val muscleGroups:   List<MuscleGroupNew>,
    val equipment: EquipmentNew,
)