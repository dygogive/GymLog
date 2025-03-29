package com.example.gymlog.model.exercise;

import android.content.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public interface TypeAttributeExercises {
    public int getIconResId();
    public int getDescriptionResId();
    String getDescription(Context context);

    public static <E extends Enum<E> & TypeAttributeExercises> List<TypeAttributeExercises> getEnumItems(Class<E> enumClass) {
        return Arrays.asList(Objects.requireNonNull(enumClass.getEnumConstants()));
    }
}
