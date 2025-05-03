package com.example.gymlog.domain.model.workout


data class WorkoutResult(
    val id: Long? = 0,
    val exerciseInBlockId: Long,
    val weight: Int?,
    val iteration: Int?,
    val workTime: Int?,
    val position: Int,
    val sequenceInGymDay: Int,
    val timeFromStart: Int,
    val workoutDateTime: String // Формат "yyyy-MM-dd HH:mm"
)
