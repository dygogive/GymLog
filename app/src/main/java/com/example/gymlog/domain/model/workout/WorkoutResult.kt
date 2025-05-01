package com.example.gymlog.domain.model.workout


// WorkoutExercise - вправа що виконана
data class WorkoutResult(
    val weight: Int? = null,
    val iteration: Int? = null,
    val workTime: Int? = null,
    val sequenceInGymDay: Int,
    val position: Int,
    val timeFromStart: Int,
)
