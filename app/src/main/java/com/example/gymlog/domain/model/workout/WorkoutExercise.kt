package com.example.gymlog.domain.model.workout

import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup

// WorkoutExercise - вправа що виконана
data class WorkoutExercise(
    val name: String, // назва вправи
    val description: String?, //опис
    val motion: Motion, //тип руху
    val muscleGroups: List<MuscleGroup>, //групи м'язів
    val equipment: Equipment,
    val results: List<WorkoutResult>,
    val position: Int,
)