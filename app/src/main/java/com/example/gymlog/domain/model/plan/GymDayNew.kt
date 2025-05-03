package com.example.gymlog.domain.model.plan


data class GymDayNew(
    val id: Long = 0,
    val name: String,
    val description: String,
    val position: Int?,
    val trainingBlocks: List<TrainingBlockNew>
)