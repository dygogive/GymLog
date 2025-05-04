package com.example.gymlog.ui.feature.workout.model


data class ProgramInfo(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val gymDayUiModels: List<GymDayUiModel>
)
