package com.example.gymlog.domain.model.workout

// WorkoutSet модель
data class WorkoutSet(
    val id: Long?,
    val workoutId: Long?,
    val trainingBlockId: Long?,
    val name: String?,
    val description: String?,
    val position: Int?
)