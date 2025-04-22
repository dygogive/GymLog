package com.example.gymlog.domain.model.workout


// WorkoutExercise - вправа що виконана
data class WorkoutResult(
    val id: Long?, //ід результату
    val workoutExerciseId: Long, // ід вправи WorkoutExercise
    val weight: Int?, // вага
    val iteration: Int?, // к-сть повторень
    val workTime: Int?, //тривалість виконання в секундах
    val orderInWorkoutExercise: Int, // який по черзі цей результат у цьому ж тренуванні
    val orderInWorkSet: Int, // який по черзі цей результат у цьому ж блоці WorkoutSet
    val orderInWorkGymDay: Int, // яке по черзі у всьому тренуванні
    val minutesSinceStartWorkout: Int, // минуло хвилин після старту тренування на момент запису цього результату
    val date: String, // минуло хвилин після старту тренування на момент запису цього результату
)