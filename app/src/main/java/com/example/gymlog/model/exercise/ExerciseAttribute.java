package com.example.gymlog.model.exercise;

import android.content.Context;

public interface ExerciseAttribute {
    int getDescriptionResId();
    String getDescription(Context context);
}
