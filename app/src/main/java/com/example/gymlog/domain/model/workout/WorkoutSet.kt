package com.example.gymlog.domain.model.workout

import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew

// WorkoutSet виконаний блок тренування
data class WorkoutSet(
    val name: String, //назва - збігається з назвою шаблону трен блоку
    val description: String?,//опис - збігається з назвоб шаблону трен блоку
    val workoutExercises: List<WorkoutExercise>, //виконані вправи у цьому блоці
    val muscleGroups: List<MuscleGroupNew>, //групи м'язів
    val equipments: List<EquipmentNew>,
    val motions: List<MotionNew>,
    val results: List<WorkoutResult>,
    val position: Int, //позиція виконання на тренуванні
)