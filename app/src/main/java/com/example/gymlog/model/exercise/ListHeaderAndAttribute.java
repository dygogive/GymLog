package com.example.gymlog.model.exercise;

public abstract class ListHeaderAndAttribute {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    abstract public int getType();
}
