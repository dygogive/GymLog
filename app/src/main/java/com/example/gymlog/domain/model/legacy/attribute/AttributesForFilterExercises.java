package com.example.gymlog.domain.model.legacy.attribute;

import android.content.Context;
import com.example.gymlog.R;
import java.util.ArrayList;
import java.util.List;

public enum AttributesForFilterExercises implements DescriptionAttribute {
    EQUIPMENT(R.string.equipment_type, R.drawable.ic_muscle),
    MUSCLE_GROUP(R.string.muscle_group, R.drawable.ic_muscle),
    MOTION(R.string.motion, R.drawable.ic_muscle);

    private final int descriptionResId;
    private final int iconResId;

    AttributesForFilterExercises(int descriptionResId, int iconResId) {
        this.descriptionResId = descriptionResId;
        this.iconResId = iconResId;
    }

    public static AttributesForFilterExercises[] getAllValues() {
        return values();
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    @Override
    public String getDescription(Context context) {
        return context.getString(descriptionResId);
    }

    public static List<ListHeaderAndAttribute> getGroupedEquipmentItems(Context context) {
        List<ListHeaderAndAttribute> items = new ArrayList<>();

        // Додати всі фільтри категорій вправ
        for (AttributesForFilterExercises filter : values()) {
            items.add(new FilterItem<>(filter));
        }

        return items;
    }
}