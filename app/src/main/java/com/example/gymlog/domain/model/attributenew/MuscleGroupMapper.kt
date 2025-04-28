package com.example.gymlog.domain.model.attributenew

import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup

object MuscleGroupMapper {

    fun fromString(value: String?): MuscleGroup? {
        return try {
            value?.let { MuscleGroup.valueOf(it) }
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun toString(muscleGroup: MuscleGroup?): String? {
        return muscleGroup?.name
    }
}