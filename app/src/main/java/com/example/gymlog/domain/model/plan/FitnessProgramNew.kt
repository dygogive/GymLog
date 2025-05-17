package com.example.gymlog.domain.model.plan



data class FitnessProgramNew(
    val id: Long = 0,
    val uuid: String,
    val name: String,
    val description: String?,
    val position: Int?,
    val creationDate: String?,
    var gymDays: List<GymDayNew>
)



