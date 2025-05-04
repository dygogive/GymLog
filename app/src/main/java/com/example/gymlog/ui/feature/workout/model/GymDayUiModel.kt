package com.example.gymlog.ui.feature.workout.model

data class GymDayUiModel (
    val id: Long = 0,
    val name: String,
    val description: String?,
    val position: Int,
    val trainingBlocksUiModel: List<TrainingBlockUiModel>
)