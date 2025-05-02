package com.example.gymlog.ui.feature.workout.model

data class GymDayUiModel (
    val name: String,
    val description: String?,
    val position: Int,
    val trainingBlocksUiModel: List<TrainingBlockUiModel>
)