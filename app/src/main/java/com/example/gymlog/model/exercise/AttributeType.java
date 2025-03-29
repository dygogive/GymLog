package com.example.gymlog.model.exercise;

import android.content.Context;

import com.example.gymlog.R;

public enum AttributeType implements TypeAttributeExercises {

    EQUIPMENT(R.string.equipment_type),
    MUSCLE_GROUP(R.string.muscle_group),
    MOTION(R.string.motion);

    public static AttributeType[] getAllValues() {
        return values(); // <- тут вже викликається рідний метод
    }



    // Якщо хочете зберігати посилання на ресурс:
    public int getIconResId() {
        switch (this) {
            case MUSCLE_GROUP:
                return R.drawable.ic_muscle;
            case EQUIPMENT:
                return R.drawable.ic_muscle;
            case MOTION:
                return R.drawable.ic_muscle;
        }
        return R.drawable.ic_exercise; // Щось за замовчуванням
    }

    @Override
    public int getDescriptionResId() {
        return descriptionResId;
    }


    //посилання на строку що описує м'язеву групу
    private final int descriptionResId;

    //конструктор
    AttributeType(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }

    //видати текст опису м'язевої групи
    public String getDescription(Context context) {
        return context.getString(descriptionResId);
    }
}
