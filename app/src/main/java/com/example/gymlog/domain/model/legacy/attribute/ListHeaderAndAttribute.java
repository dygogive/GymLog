package com.example.gymlog.domain.model.legacy.attribute;

public abstract class ListHeaderAndAttribute {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FILTER = 2;

    abstract public int getType();
}
