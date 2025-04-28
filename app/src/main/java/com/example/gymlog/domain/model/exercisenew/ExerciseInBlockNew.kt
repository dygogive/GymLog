package com.example.gymlog.domain.model.exercisenew

import com.example.gymlog.domain.model.attribute.equipment.EquipmentNew
import com.example.gymlog.domain.model.attribute.motion.MotionNew
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroupNew

class ExerciseInBlockNew (
    name:           String,
    description:    String,
    motion: MotionNew,
    muscleGroups:   List<MuscleGroupNew>,
    equipment: EquipmentNew,
    val position:   Int
): ExerciseNew(
    name,
    description,
    motion,
    muscleGroups,
    equipment
)