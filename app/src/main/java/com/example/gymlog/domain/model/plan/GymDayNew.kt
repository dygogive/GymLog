package com.example.gymlog.domain.model.plan


data class GymDayNew(
    val name: String,
    val description: String,
    val position: Int?,
    val trainingBlocks: List<TrainingBlockNew>
)