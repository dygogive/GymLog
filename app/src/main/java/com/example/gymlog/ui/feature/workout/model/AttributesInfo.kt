package com.example.gymlog.ui.feature.workout.model

data class AttributesInfo(
    val motionStateList: MotioStateList,
    val muscleStateList: MusclesStateList,
    val equipmentStateList: EquipmentStateList,
)


data class MotioStateList(
    val motions: List<String>,
)

data class MusclesStateList(
    val muscles: List<String>,
)

data class EquipmentStateList(
    val equipments: List<String>,
)