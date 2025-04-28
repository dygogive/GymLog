package com.example.gymlog.domain.model.attributenew

import com.example.gymlog.domain.model.attribute.equipment.Equipment

object EquipmentMapper {

    fun fromString(value: String?): Equipment? {
        return try {
            value?.let { Equipment.valueOf(it) }
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun toString(equipment: Equipment?): String? {
        return equipment?.name
    }
}