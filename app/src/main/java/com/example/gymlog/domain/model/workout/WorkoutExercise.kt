package com.example.gymlog.domain.model.workout

// WorkoutExercise модель
data class WorkoutExercise(
    val id: Long?,
    val workoutGymDayId: Long?,
    val exerciseId: Long?,
    val name: String?,
    val description: String?,
    val motion: String?,
    val muscleGroups: String?,
    val equipment: String?
)