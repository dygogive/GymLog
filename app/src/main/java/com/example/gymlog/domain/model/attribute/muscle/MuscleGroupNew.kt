package com.example.gymlog.domain.model.attribute.muscle

sealed interface MuscleGroupNew {
    val name:           String
    val description:    String

    // Груди
    data class ChestLower(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class ChestUpper(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class Chest(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    // Руки
    data class Triceps(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class Biceps(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    // Трапеції
    data class TrapsUpper(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class TrapsMiddle(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class TrapsLower(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    // Спина
    data class Lats(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class Longissimus(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    // Ноги
    data class Hamstrings(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class Quadriceps(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class Glutes(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    // Дельти
    data class DeltsRear(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class DeltsSide(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew

    data class DeltsFront(
        override val name:          String,
        override val description:   String
    ) : MuscleGroupNew
}