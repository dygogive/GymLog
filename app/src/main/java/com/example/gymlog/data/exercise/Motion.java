package com.example.gymlog.data.exercise;

import android.content.Context;

import com.example.gymlog.R;

public enum Motion {

    //ОСНОВНІ РУХИ ПРИ ВИКОНАННІ ВПРАВ
    PRESS_BY_ARMS(R.string.motion_press_by_arms),
    PULL_BY_ARMS(R.string.motion_pull_by_arms),
    PRESS_BY_LEGS(R.string.motion_press_by_legs),
    PULL_BY_LEGS(R.string.motion_pull_by_legs);

    //посилання на строку що описує м'язеву групу
    private final int descriptionResId;

    //конструктор
    Motion(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }

    //видати ідентифікатор опису м'язевої групи
    public int getDescriptionResId() {
        return descriptionResId;
    }

    //видати текст опису м'язевої групи
    public String getDescription(Context context) {
        return context.getString(descriptionResId);
    }

    //Щоб сформувати масив String[] з описів для кожного елемента перерахування Motion
    public static String[] getMotionDescriptions(Context context) {
        Motion[] motions = Motion.values();  // Отримуємо всі елементи перерахування
        String[] descriptions = new String[motions.length];  // Створюємо масив для зберігання описів

        // Заповнюємо масив описами для кожного руху
        for (int i = 0; i < motions.length; i++) {
            descriptions[i] = motions[i].getDescription(context);
        }

        return descriptions;  // Повертаємо масив з описами
    }

}
