package com.example.gymlog.domain.model.plannew


// domain/model/plan/FitnessProgramNew.kt
data class FitnessProgramNew(
    val name: String,
    val description: String?,
    val position: Int,
    val creationDate: String?,
    val gymDays: List<GymDayNew>
)



