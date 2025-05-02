package com.example.gymlog.domain.model.exercise


import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew

class ExerciseInBlockNew (
    name: String,
    description: String,
    motion: MotionNew,
    muscleGroups: List<MuscleGroupNew>,
    equipment: EquipmentNew,
    isCustom: Boolean,
    val position: Int,
): ExerciseNew(
    name,
    description,
    motion,
    muscleGroups,
    equipment,
    isCustom
)