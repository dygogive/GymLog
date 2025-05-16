package com.example.gymlog.ui.feature.workout.model

data class ExerciseBlockUI(
    val exerciseId: Long = 0,
    val name: String = "name exercise",
    val description: String = "",
    val results: List<ResultOfSet>,
)
