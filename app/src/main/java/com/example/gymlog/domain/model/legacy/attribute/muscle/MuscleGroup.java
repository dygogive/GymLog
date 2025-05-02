package com.example.gymlog.domain.model.legacy.attribute.muscle;

import android.content.Context;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.legacy.attribute.AttributeItem;
import com.example.gymlog.domain.model.legacy.attribute.HeaderItem;
import com.example.gymlog.domain.model.legacy.attribute.ListHeaderAndAttribute;
import com.example.gymlog.domain.model.legacy.attribute.TypeAttributeExercises;

import java.util.ArrayList;
import java.util.List;


public enum MuscleGroup implements TypeAttributeExercises {

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



    public static List<ListHeaderAndAttribute> getGroupedEquipmentItems(Context context) {
        List<ListHeaderAndAttribute> items = new ArrayList<>();

        items.add(new HeaderItem(context.getString(R.string.muscle_chest)));
        items.add(new AttributeItem<>(CHEST_LOWER));
        items.add(new AttributeItem<>(CHEST_UPPER));
        items.add(new AttributeItem<>(CHEST));

        items.add(new HeaderItem(context.getString(R.string.muscles_traps)));
        items.add(new AttributeItem<>(TRAPS_UPPER));
        items.add(new AttributeItem<>(TRAPS_MIDDLE));
        items.add(new AttributeItem<>(TRAPS_LOWER));

        items.add(new HeaderItem(context.getString(R.string.muscles_back_other)));
        items.add(new AttributeItem<>(LATS));
        items.add(new AttributeItem<>(LONGISSIMUS));

        items.add(new HeaderItem(context.getString(R.string.muscles_arms)));
        items.add(new AttributeItem<>(TRICEPS));
        items.add(new AttributeItem<>(BICEPS));

        items.add(new HeaderItem(context.getString(R.string.muscles_delts)));
        items.add(new AttributeItem<>(DELTS_REAR));
        items.add(new AttributeItem<>(DELTS_SIDE));
        items.add(new AttributeItem<>(DELTS_FRONT));

        items.add(new HeaderItem(context.getString(R.string.muscles_legs)));
        items.add(new AttributeItem<>(GLUTES));
        items.add(new AttributeItem<>(HAMSTRINGS));
        items.add(new AttributeItem<>(QUADRICEPS));

        return items;
    }

}
