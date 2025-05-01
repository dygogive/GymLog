package com.example.gymlog.domain.model.workout

import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup

// WorkoutSet виконаний блок тренування
data class WorkoutSet(
    val name: String, //назва - збігається з назвою шаблону трен блоку
    val description: String?,//опис - збігається з назвоб шаблону трен блоку
    val workoutExercises: List<WorkoutExercise>, //виконані вправи у цьому блоці
    val muscleGroups: List<MuscleGroup>, //групи м'язів
    val equipment: Equipment,
    val results: List<WorkoutResult>,
    val position: Int, //позиція виконання на тренуванні
)