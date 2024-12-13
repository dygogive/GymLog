package com.example.gymlog.data;

import android.content.Context;

import com.example.gymlog.R;

import java.util.ArrayList;
import java.util.List;

public enum MuscleGroup {
    CHEST_LOWER(R.string.muscle_chest_lower),
    CHEST_UPPER(R.string.muscle_chest_upper),
    CHEST_FULL(R.string.muscle_chest_full),

    TRICEPS(R.string.muscle_triceps),
    BICEPS_ARMS(R.string.muscle_biceps_arms),

    TRAPS_UPPER(R.string.muscle_traps_upper),
    TRAPS_MIDDLE(R.string.muscle_traps_middle),
    TRAPS_LOWER(R.string.muscle_traps_lower),
    LATS(R.string.muscle_lats),

    HAMSTRINGS(R.string.muscle_hamstrings),
    QUADRICEPS(R.string.muscle_quadriceps),
    GLUTES(R.string.muscle_glutes),

    DELTS_REAR(R.string.muscle_delts_rear),
    DELTS_SIDE(R.string.muscle_delts_side),
    DELTS_FRONT(R.string.muscle_delts_front),

    LONGISSIMUS(R.string.muscle_longissimus);

    private final int descriptionResId;

    MuscleGroup(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public String getDescription(Context context) {
        return context.getString(descriptionResId);
    }


}
