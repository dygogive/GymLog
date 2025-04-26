package com.example.gymlog.domain.model.attribute.motion;

import android.content.Context;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.attribute.AttributeItem;
import com.example.gymlog.domain.model.attribute.HeaderItem;
import com.example.gymlog.domain.model.attribute.ListHeaderAndAttribute;
import com.example.gymlog.domain.model.attribute.TypeAttributeExercises;

import java.util.ArrayList;
import java.util.List;

public enum Motion  implements TypeAttributeExercises {

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


    public int getDescriptionResId() {
        return descriptionResId;
    }


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



    public static List<ListHeaderAndAttribute> getGroupedEquipmentItems(Context context) {
        List<ListHeaderAndAttribute> items = new ArrayList<>();

        items.add(new HeaderItem(context.getString(R.string.press)));
        items.add(new AttributeItem<>(PRESS_UPWARDS));
        items.add(new AttributeItem<>(PRESS_DOWNWARDS));
        items.add(new AttributeItem<>(PRESS_MIDDLE));
        items.add(new AttributeItem<>(PRESS_UP_MIDDLE));
        items.add(new AttributeItem<>(PRESS_DOWN_MIDDLE));

        items.add(new HeaderItem(context.getString(R.string.pull)));
        items.add(new AttributeItem<>(PULL_UPWARDS));
        items.add(new AttributeItem<>(PULL_DOWNWARDS));
        items.add(new AttributeItem<>(PULL_MIDDLE));
        items.add(new AttributeItem<>(PULL_UP_MIDDLE));
        items.add(new AttributeItem<>(PULL_DOWN_MIDDLE));

        items.add(new HeaderItem(context.getString(R.string.press_pull_by_legs)));
        items.add(new AttributeItem<>(PRESS_BY_LEGS));
        items.add(new AttributeItem<>(PULL_BY_LEGS));

        return items;
    }

}
