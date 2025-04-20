package com.example.gymlog.domain.model.workout

// WorkoutSet виконаний блок тренування
data class WorkoutSet(
    val id: Long?, // ід в базі
    val workoutId: Long?, //ід виконаного тренування
    val trainingBlockId: Long?, //ід тренувального блоку, що є шаблоном цього
    val name: String, //назва - збігається з назвоб шаблону трен блоку
    val description: String?,//опис - збігається з назвоб шаблону трен блоку
    val position: Int, //позиція в тренуванні - збігається з позицією в шаблоні трен блоку
    val physicalСondition: Int?, //суб'єктивна оцінка фізичних кондицій при виконанні блоку 1...5
    val comments: String? //записані коментарі
)