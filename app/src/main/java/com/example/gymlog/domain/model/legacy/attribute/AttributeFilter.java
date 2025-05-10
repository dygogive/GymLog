package com.example.gymlog.domain.model.legacy.attribute;

import android.content.Context;

import com.example.gymlog.R;

import java.util.ArrayList;
import java.util.List;

public enum AttributeFilter implements DescriptionAttribute {

    EQUIPMENT(R.string.equipment_type),
    MUSCLE_GROUP(R.string.muscle_group),
    MOTION(R.string.motion);

    public static AttributeFilter[] getAllValues() {
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


    public int getDescriptionResId() {
        return descriptionResId;
    }


    //посилання на строку що описує м'язеву групу
    private final int descriptionResId;

    //конструктор
    AttributeFilter(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }

    //видати текст опису м'язевої групи
    public String getDescription(Context context) {
        return context.getString(descriptionResId);
    }


    public static List<ListHeaderAndAttribute> getGroupedEquipmentItems(Context context) {
        List<ListHeaderAndAttribute> items = new ArrayList<>();


        items.add(new FiltreAttributes<>(MUSCLE_GROUP));
        items.add(new FiltreAttributes<>(EQUIPMENT));
        items.add(new FiltreAttributes<>(MOTION));

        return items;
    }
}
