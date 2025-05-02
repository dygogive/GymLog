package com.example.gymlog.domain.model.legacy.plan

import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup

data class BlockFilters(
    val motions: List<Motion>,
    val muscleGroups: List<MuscleGroup>,
    val equipment: List<Equipment>
)