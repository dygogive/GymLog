package com.example.gymlog.domain.model.workout

// WorkoutGymDay модель
data class WorkoutGymDay(
    val id: Long?,
    val datetime: Long?,
    val plansId: Long?,
    val gymDaysId: Long?,
    val sets: Int?,
    val blocks: Int?,
    val minutes: Int?,
    val name: String?,
    val description: String?
)