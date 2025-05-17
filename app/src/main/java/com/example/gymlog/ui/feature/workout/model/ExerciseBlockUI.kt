package com.example.gymlog.ui.feature.workout.model

import kotlin.random.Random


data class ExerciseBlockUI(
    val linkId: Long,
    val exerciseId: Long = 0,
    val name: String = "name exercise",
    val description: String = "",
    val results: List<ResultOfSet>,
)
