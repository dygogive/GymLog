package com.example.gymlog.ui.feature.workout.model

data class AttributesInfo(
    val motionStateList: MotionStateList,
    val muscleStateList: MusclesStateList,
    val equipmentStateList: EquipmentStateList,
)


data class MotionStateList(
    val motions: List<String>,
)

data class MusclesStateList(
    val muscles: List<String>,
)

data class EquipmentStateList(
    val equipments: List<String>,
)