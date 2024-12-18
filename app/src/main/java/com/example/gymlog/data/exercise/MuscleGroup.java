package com.example.gymlog.data.exercise;

import android.content.Context;

import com.example.gymlog.R;


public enum MuscleGroup {

    //грудні м'язи
    CHEST_LOWER(R.string.muscle_chest_lower),
    CHEST_UPPER(R.string.muscle_chest_upper),
    CHEST(R.string.muscle_chest),

    //руки
    TRICEPS(R.string.muscle_triceps),
    BICEPS(R.string.muscle_biceps_arms),

    //спина
    TRAPS_UPPER(R.string.muscle_traps_upper),
    TRAPS_MIDDLE(R.string.muscle_traps_middle),
    TRAPS_LOWER(R.string.muscle_traps_lower),
    LATS(R.string.muscle_lats),
    LONGISSIMUS(R.string.muscle_longissimus),

    //ноги
    HAMSTRINGS(R.string.muscle_hamstrings),
    QUADRICEPS(R.string.muscle_quadriceps),
    GLUTES(R.string.muscle_glutes),

    //плечі
    DELTS_REAR(R.string.muscle_delts_rear),
    DELTS_SIDE(R.string.muscle_delts_side),
    DELTS_FRONT(R.string.muscle_delts_front);


    //посилання на строку що описує м'язеву групу
    private final int descriptionResId;

    //конструктор
    MuscleGroup(int descriptionResId) {
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

    //Щоб сформувати масив String[] з описів для кожного елемента перерахування MuscleGroup
    public static String[] getMuscleGroupDescriptions(Context context) {
        MuscleGroup[] muscleGroups = MuscleGroup.values();  // Отримуємо всі елементи перерахування
        String[] descriptions = new String[muscleGroups.length];  // Створюємо масив для зберігання описів

        // Заповнюємо масив описами для кожного руху
        for (int i = 0; i < muscleGroups.length; i++) {
            descriptions[i] = muscleGroups[i].getDescription(context);
        }

        return descriptions;  // Повертаємо масив з описами
    }

}
