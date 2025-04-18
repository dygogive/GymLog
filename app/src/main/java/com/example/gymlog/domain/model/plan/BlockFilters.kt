package com.example.gymlog.domain.model.plan

import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup

data class BlockFilters(
    val motions: List<Motion>,
    val muscleGroups: List<MuscleGroup>,
    val equipment: List<Equipment>
)