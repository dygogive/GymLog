package com.example.gymlog.domain.model.attribute

data class BlockFilters(
    val motions: List<MotionNew>,
    val muscleGroups: List<MuscleGroupNew>,
    val equipment: List<EquipmentNew>
)