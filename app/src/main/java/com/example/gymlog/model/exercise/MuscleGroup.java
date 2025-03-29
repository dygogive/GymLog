package com.example.gymlog.model.exercise;

import android.content.Context;

import com.example.gymlog.R;


public enum MuscleGroup implements TypeAttributeExercises  {

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
    DELTS_FRONT(R.string.muscle_delts_front);

    public static MuscleGroup[] getAllValues() {
        return values(); // <- тут вже викликається рідний метод
    }

    private final int descriptionResId;

    MuscleGroup(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }

    @Override
    public int getDescriptionResId() {
        return descriptionResId;
    }

    @Override
    public String getDescription(Context context) {
        return context.getString(descriptionResId);
    }

    //Щоб сформувати масив String[] з описів для кожного елемента перерахування MuscleGroup
    public static String[] getAllDescriptions(Context context) {
        MuscleGroup[] groups = values();
        String[] descriptions = new String[groups.length];
        for (int i = 0; i < groups.length; i++) {
            descriptions[i] = groups[i].getDescription(context);
        }
        return descriptions;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_exercise;
    }


    public static MuscleGroup getObjectByDescription(Context context, String description) {
        for (MuscleGroup group : values()) {
            if (group.getDescription(context).equals(description)) {
                return group;
            }
        }
        return null;
    }

}
