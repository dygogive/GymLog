package com.example.gymlog.domain.model.exercisenew


import com.example.gymlog.domain.model.attributenew.EquipmentNew
import com.example.gymlog.domain.model.attributenew.MotionNew
import com.example.gymlog.domain.model.attributenew.MuscleGroupNew

class ExerciseInBlockNew (
    name: String,
    description: String,
    motion: MotionNew,
    muscleGroups: List<MuscleGroupNew>,
    equipment: EquipmentNew,
    val position: Int
): ExerciseNew(
    name,
    description,
    motion,
    muscleGroups,
    equipment
)