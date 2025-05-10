package com.example.gymlog.domain.model.legacy.attribute;

public class FilterItem<E extends Enum<E> & DescriptionAttribute> extends ListHeaderAndAttribute {
    private final E attributeEnum;

    public FilterItem(E attributeEnum) {
        this.attributeEnum = attributeEnum;
    }

    public E getAttribute() {
        return attributeEnum;
    }

    @Override
    public int getType() {
        return TYPE_FILTER;
    }
}