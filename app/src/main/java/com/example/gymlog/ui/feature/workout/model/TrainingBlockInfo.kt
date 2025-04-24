package com.example.gymlog.ui.feature.workout.model

data class TrainingBlockInfo (
    val name: String = "name training block",
    val description: String = "",
    val attributesInfo: AttributesInfo,
    val infoExercises: List<ExerciseInfo>,
)



