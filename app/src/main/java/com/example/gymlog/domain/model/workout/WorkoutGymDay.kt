package com.example.gymlog.domain.model.workout

// WorkoutGymDay - тренування , що виконане
data class WorkoutGymDay(
    val name: String, // назва тренування (збігається з шаблоном тренування (GymSession)
    val description: String?, //опис тренування (збігається з шаблоном тренування (GymSession)
    val workoutSets: List<WorkoutSet>, //
    val minutes: Int?, //тривалість тренування у хв
    val datetime: Long, //дата й час
)