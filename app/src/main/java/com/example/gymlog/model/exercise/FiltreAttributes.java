package com.example.gymlog.model.exercise;

public class FiltreAttributes<E extends Enum<E> & TypeAttributeExercises> extends ListHeaderAndAttribute {
    private final E attributeEnum;

    public FiltreAttributes(E attributeEnum) {
        this.attributeEnum = attributeEnum;
    }

    public E getEquipment() {
        return attributeEnum;
    }

    @Override
    public int getType() {
        return TYPE_FILTER;
    }
}
