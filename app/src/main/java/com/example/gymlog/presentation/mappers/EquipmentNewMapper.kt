package com.example.gymlog.presentation.mappers


import com.example.gymlog.R
import com.example.gymlog.domain.model.attribute.EquipmentNew

/**
 * Mapper для перетворення EquipmentNew enum у відповідний ресурс рядка
 */
object EquipmentNewMapper {

    /**
     * Повертає ID ресурсу рядка для вказаного обладнання
     * @param equipment тип обладнання
     * @return ID ресурсу рядка
     */
    fun mapToStringResource(equipment: EquipmentNew): Int {
        return when (equipment) {
            EquipmentNew.BARBELL -> R.string.equipment_barbell
            EquipmentNew.DUMBBELLS -> R.string.equipment_dumbbells
            EquipmentNew.KETTLEBELL -> R.string.equipment_kettlebell
            EquipmentNew.WEIGHT_PLATE -> R.string.equipment_weight_plate
            EquipmentNew.BODYWEIGHT -> R.string.equipment_bodyweight
            EquipmentNew.MACHINE -> R.string.equipment_machine
            EquipmentNew.CABLE_MACHINE -> R.string.equipment_cable_machine
            EquipmentNew.PLATE_LOADED_MACHINE -> R.string.equipment_plate_loaded_machine
            EquipmentNew.RESISTANCE_BAND -> R.string.equipment_resistance_band
            EquipmentNew.SANDBAG -> R.string.equipment_sandbag
            EquipmentNew.MEDICINE_BALL -> R.string.equipment_medicine_ball
            EquipmentNew.SUSPENSION_TRAINER -> R.string.equipment_suspension_trainer
        }
    }
}