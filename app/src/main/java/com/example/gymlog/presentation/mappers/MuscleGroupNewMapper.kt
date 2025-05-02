package com.example.gymlog.presentation.mappers

import com.example.gymlog.R
import com.example.gymlog.domain.model.attribute.MuscleGroupNew

/**
 * Mapper для перетворення MuscleGroupNew enum у відповідний ресурс рядка
 */
object MuscleGroupNewMapper {

    /**
     * Повертає ID ресурсу рядка для вказаної групи м'язів
     * @param muscleGroup група м'язів
     * @return ID ресурсу рядка
     */
    fun mapToStringResource(muscleGroup: MuscleGroupNew): Int {
        return when (muscleGroup) {
            MuscleGroupNew.CHEST_LOWER -> R.string.muscle_chest_lower
            MuscleGroupNew.CHEST_UPPER -> R.string.muscle_chest_upper
            MuscleGroupNew.CHEST -> R.string.muscle_chest
            MuscleGroupNew.TRICEPS -> R.string.muscle_triceps
            MuscleGroupNew.BICEPS -> R.string.muscle_biceps_arms
            MuscleGroupNew.TRAPS_UPPER -> R.string.muscle_traps_upper
            MuscleGroupNew.TRAPS_MIDDLE -> R.string.muscle_traps_middle
            MuscleGroupNew.TRAPS_LOWER -> R.string.muscle_traps_lower
            MuscleGroupNew.LATS -> R.string.muscle_lats
            MuscleGroupNew.LONGISSIMUS -> R.string.muscle_longissimus
            MuscleGroupNew.HAMSTRINGS -> R.string.muscle_hamstrings
            MuscleGroupNew.QUADRICEPS -> R.string.muscle_quadriceps
            MuscleGroupNew.GLUTES -> R.string.muscle_glutes
            MuscleGroupNew.DELTS_REAR -> R.string.muscle_delts_rear
            MuscleGroupNew.DELTS_SIDE -> R.string.muscle_delts_side
            MuscleGroupNew.DELTS_FRONT -> R.string.muscle_delts_front
        }
    }
}