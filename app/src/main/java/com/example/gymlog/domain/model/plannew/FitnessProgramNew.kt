package com.example.gymlog.domain.model.plannew



data class FitnessProgramNew(
    val name: String,
    val description: String?,
    val position: Int?,
    val creationDate: String?,
    val gymDays: List<GymDayNew>
)



