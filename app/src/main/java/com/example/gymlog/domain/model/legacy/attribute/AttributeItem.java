package com.example.gymlog.domain.model.legacy.attribute;

public class AttributeItem<E extends Enum<E> & TypeAttributeExercises> extends ListHeaderAndAttribute {
    private final E attributeEnum;

    public AttributeItem(E attributeEnum) {
        this.attributeEnum = attributeEnum;
    }

    public E getEquipment() {
        return attributeEnum;
    }

    @Override
    public int getType() {
        return TYPE_ITEM;
    }
}