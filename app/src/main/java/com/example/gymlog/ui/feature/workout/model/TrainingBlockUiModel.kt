package com.example.gymlog.ui.feature.workout.model

data class TrainingBlockUiModel (
    val id: Long = 0,
    val name: String = "name training block",
    val description: String = "",
    val attributesInfo: AttributesInfo,
    val infoExercises: List<ExerciseBlockUI>,
    val uuid: String = "null",
)



