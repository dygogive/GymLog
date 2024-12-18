package com.example.gymlog.data.exercise;

import android.content.Context;

import com.example.gymlog.R;

public enum Equipment {

    //вага що допомагає тренуватися
    BARBELL(R.string.equipment_barbell),
    DUMBBELLS(R.string.equipment_dumbells),
    BODY_WEIGHT(R.string.equipment_bodyweight),
    MACHINE(R.string.equipment_machine),
    WEIGHT(R.string.weight);


    //посилання на строку що описує обладнання
    private final int descriptionResId;

    //конструктор
    Equipment(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }

    //видати ідентифікатор опису обладнання
    public int getDescriptionResId() {
        return descriptionResId;
    }

    //видати текст опису обладнання
    public String getDescription(Context context) {
        return context.getString(descriptionResId);
    }

    //Щоб сформувати масив String[] з описів для кожного елемента перерахування Equipment
    public static String[] getEquipmentDescriptions(Context context) {
        Equipment[] equipments = Equipment.values();  // Отримуємо всі елементи перерахування
        String[] descriptions = new String[equipments.length];  // Створюємо масив для зберігання описів

        // Заповнюємо масив описами для кожного руху
        for (int i = 0; i < equipments.length; i++) {
            descriptions[i] = equipments[i].getDescription(context);
        }

        return descriptions;  // Повертаємо масив з описами
    }
}
