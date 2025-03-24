package com.example.gymlog.model.exercise;

import android.content.Context;

import com.example.gymlog.R;

public enum AttributeType {

    EQUIPMENT(R.string.equipment_type),
    MUSCLE_GROUP(R.string.muscle_group),
    MOTION(R.string.motion);


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
