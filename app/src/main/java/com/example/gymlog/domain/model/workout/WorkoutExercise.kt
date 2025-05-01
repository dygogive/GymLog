package com.example.gymlog.domain.model.workout


import com.example.gymlog.domain.model.attributenew.EquipmentNew
import com.example.gymlog.domain.model.attributenew.MotionNew
import com.example.gymlog.domain.model.attributenew.MuscleGroupNew

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