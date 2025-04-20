package com.example.gymlog.domain.model.workout

// WorkoutGymDay - тренування , що виконане
data class WorkoutGymDay(
    val id: Long?, //ід
    val datetime: Long?, //дата й час
    val plansId: Long?, // ід плану (програми) тренувань
    val gymDaysId: Long?, // ід заготовки тренування, що  у програмі
    val sets: Int?, // кількість підходів за це тренування
    val blocks: Int?, // к-сть виконаних тренувальних блоків (WorkoutGym)
    val minutes: Int?, //тривалість тренування у хв
    val name: String?, // назва тренування (збігається з шаблоном тренування (GymSession)
    val description: String? //опис тренування (збігається з шаблоном тренування (GymSession)
)