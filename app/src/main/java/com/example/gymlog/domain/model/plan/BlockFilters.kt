package com.example.gymlog.domain.model.plan

import com.example.gymlog.domain.model.exercise.Equipment
import com.example.gymlog.domain.model.exercise.Motion
import com.example.gymlog.domain.model.exercise.MuscleGroup

data class BlockFilters(
    val motions: List<Motion>,
    val muscleGroups: List<MuscleGroup>,
    val equipment: List<Equipment>
)