package com.example.gymlog.ui.feature.workout.model

data class ExerciseInfo(
    val name: String = "name exercise",
    val description: String = "",
    val results: List<ResultOfSet>,
)
