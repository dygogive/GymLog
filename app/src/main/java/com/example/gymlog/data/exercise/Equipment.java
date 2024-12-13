package com.example.gymlog.data.exercise;

import android.content.Context;

import com.example.gymlog.R;

public enum Equipment {

    //вага що допомагає тренуватися
    BARBELL(R.string.equipment_barbell),
    DUMBBELLS(R.string.equipment_dumbells),
    BODY_WEIGHT(R.string.equipment_bodyweight),
    MACHINE(R.string.equipment_machine);


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
}
