package com.example.gymlog.model.exercise;

import android.content.Context;

import com.example.gymlog.R;

public enum Motion  implements  TypeAttributeExercises {

    PRESS_UPWARDS(R.string.motion_press_upwards),
    PRESS_DOWNWARDS(R.string.motion_press_downwards),
    PRESS_MIDDLE(R.string.motion_press_middle),
    PRESS_UP_MIDDLE(R.string.motion_press_up_middle),
    PRESS_DOWN_MIDDLE(R.string.motion_press_down_middle),
    PULL_UPWARDS(R.string.motion_pull_upwards),
    PULL_DOWNWARDS(R.string.motion_pull_downwards),
    PULL_MIDDLE(R.string.motion_pull_middle),
    PULL_UP_MIDDLE(R.string.motion_pull_up_middle),
    PULL_DOWN_MIDDLE(R.string.motion_pull_down_middle),
    PRESS_BY_LEGS(R.string.motion_press_by_legs),
    PULL_BY_LEGS(R.string.motion_pull_by_legs);


    public static Motion[] getAllValues() {
        return values(); // <- тут вже викликається рідний метод
    }

    private final int descriptionResId;

    Motion(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }

    @Override
    public int getDescriptionResId() {
        return descriptionResId;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_exercise;
    }

    @Override
    public String getDescription(Context context) {
        return context.getString(descriptionResId);
    }

    //Щоб сформувати масив String[] з описів для кожного елемента перерахування Motion
    public static String[] getAllDescriptions(Context context) {
        Motion[] motions = Motion.values();
        String[] descriptions = new String[motions.length];
        for (int i = 0; i < motions.length; i++) {
            descriptions[i] = motions[i].getDescription(context);
        }
        return descriptions;
    }

    public static Motion getObjectByDescription(Context context, String description) {
        for (Motion motion : values()) {
            if (motion.getDescription(context).equals(description)) {
                return motion;
            }
        }
        return null;
    }

}
