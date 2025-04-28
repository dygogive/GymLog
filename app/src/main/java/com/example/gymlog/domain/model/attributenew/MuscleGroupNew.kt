package com.example.gymlog.domain.model.attributenew

import androidx.annotation.StringRes
import com.example.gymlog.R

enum class MuscleGroupNew(@StringRes val resId: Int) {
    CHEST_LOWER(R.string.muscle_chest_lower),
    CHEST_UPPER(R.string.muscle_chest_upper),
    CHEST(R.string.muscle_chest),
    TRICEPS(R.string.muscle_triceps),
    BICEPS(R.string.muscle_biceps_arms),
    TRAPS_UPPER(R.string.muscle_traps_upper),
    TRAPS_MIDDLE(R.string.muscle_traps_middle),
    TRAPS_LOWER(R.string.muscle_traps_lower),
    LATS(R.string.muscle_lats),
    LONGISSIMUS(R.string.muscle_longissimus),
    HAMSTRINGS(R.string.muscle_hamstrings),
    QUADRICEPS(R.string.muscle_quadriceps),
    GLUTES(R.string.muscle_glutes),
    DELTS_REAR(R.string.muscle_delts_rear),
    DELTS_SIDE(R.string.muscle_delts_side),
    DELTS_FRONT(R.string.muscle_delts_front)
}