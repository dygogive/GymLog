package com.example.gymlog.domain.model.attributenew

import androidx.annotation.StringRes
import com.example.gymlog.R

enum class EquipmentNew(@StringRes val resId: Int) {
    BARBELL(R.string.equipment_barbell),
    DUMBBELLS(R.string.equipment_dumbbells),
    KETTLEBELL(R.string.equipment_kettlebell),
    WEIGHT_PLATE(R.string.equipment_weight_plate),
    BODYWEIGHT(R.string.equipment_bodyweight),
    MACHINE(R.string.equipment_machine),
    CABLE_MACHINE(R.string.equipment_cable_machine),
    PLATE_LOADED_MACHINE(R.string.equipment_plate_loaded_machine),
    RESISTANCE_BAND(R.string.equipment_resistance_band),
    SANDBAG(R.string.equipment_sandbag),
    MEDICINE_BALL(R.string.equipment_medicine_ball),
    SUSPENSION_TRAINER(R.string.equipment_suspension_trainer)
}