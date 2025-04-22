package com.example.gymlog.domain.model.workout

// WorkoutExercise - вправа що виконана
data class WorkoutExercise(
    val id: Long?, //ід вправи
    val workoutGymDayId: Long?, //ід шаблону тренувального дня (тренування)
    val exerciseId: Long?, // ід вправи в списку вправ Exercise
    val name: String, // назва вправи
    val description: String?, //опис
    val motion: String, //тип руху
    val muscleGroups: String, //групи м'язів
    val equipment: String,
    val comments: String? //записані коментарі
)