package com.example.gymlog.domain.model.plannew


data class GymDayNew(
    val name: String,
    val description: String,
    val position: Int?,
    val trainingBlocks: List<TrainingBlockNew>
)