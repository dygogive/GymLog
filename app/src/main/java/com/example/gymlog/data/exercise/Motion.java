package com.example.gymlog.data.exercise;

import android.content.Context;

import com.example.gymlog.R;

public enum Motion {

    //ОСНОВНІ РУХИ ПРИ ВИКОНАННІ ВПРАВ
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

    public static Motion getMotionByDescription(Context context, String description) {
        Motion[] motions = Motion.values();  // Отримуємо всі елементи перерахування

        // Проходимо по всіх елементах перерахування
        for (Motion motion : motions) {
            // Порівнюємо опис кожного елемента з переданим рядком
            if (motion.getDescription(context).equals(description)) {
                return motion;  // Повертаємо відповідний елемент enum
            }
        }

        return null;  // Якщо не знайдено відповідного опису, повертаємо null
    }

}
