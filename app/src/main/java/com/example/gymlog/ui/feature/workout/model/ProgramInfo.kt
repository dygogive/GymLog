package com.example.gymlog.ui.feature.workout.model


data class ProgramInfo(
    val name: String,
    val description: String?,
    val gymDayUiModels: List<GymDayUiModel>
)
