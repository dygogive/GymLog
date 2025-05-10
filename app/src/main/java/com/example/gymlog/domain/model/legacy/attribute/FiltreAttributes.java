package com.example.gymlog.domain.model.legacy.attribute;

public class FiltreAttributes<E extends Enum<E> & DescriptionAttribute> extends ListHeaderAndAttribute {
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
