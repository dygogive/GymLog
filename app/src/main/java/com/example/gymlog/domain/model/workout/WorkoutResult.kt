package com.example.gymlog.domain.model.workout


// WorkoutExercise - вправа що виконана
data class WorkoutResult(
    val id: Long? = null,
    val workoutExerciseId: Long = 0L,
    val weight: Int? = null,
    val iteration: Int? = null,
    val workTime: Int? = null,
    val orderInWorkoutExercise: Int = 0,
    val orderInWorkSet: Int = 0,
    val orderInWorkGymDay: Int = 0,
    val minutesSinceStartWorkout: Int = 0,
    val date: String = ""
)
