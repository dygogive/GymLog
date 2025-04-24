package com.example.gymlog.ui.feature.workout.model

data class AttributesInfo(
    val motion: Motion,
    val muscle: Muscles,
    val equipment: Equipment,
)


data class Motion(
    val motions: List<String>,
)

data class Muscles(
    val muscles: List<String>,
)

data class Equipment(
    val equipments: List<String>,
)