package com.example.gymlog.domain.model.attribute.equipment

sealed interface EquipmentNew {
    val name:           String
    val description:    String

    // Вільна вага
    data class Barbell(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    data class Dumbbells(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    data class Kettlebell(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    data class WeightPlate(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    // Тренажери
    data class Bodyweight(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    data class Machine(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    data class CableMachine(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    data class PlateLoadedMachine(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    // Інші види
    data class ResistanceBand(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    data class Sandbag(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    data class MedicineBall(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew

    data class SuspensionTrainer(
        override val name:          String,
        override val description:   String
    ) : EquipmentNew
}