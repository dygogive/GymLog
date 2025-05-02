package com.example.gymlog.domain.model.workout


import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew

// WorkoutExercise - вправа що виконана
data class WorkoutExercise(
    val name: String, // назва вправи
    val description: String?, //опис
    val motion: MotionNew, //тип руху
    val muscleGroups: List<MuscleGroupNew>, //групи м'язів
    val equipment: EquipmentNew,
    val results: List<WorkoutResult>,
    val position: Int,
)