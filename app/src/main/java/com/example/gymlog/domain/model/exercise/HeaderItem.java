package com.example.gymlog.domain.model.exercise;

public class HeaderItem extends ListHeaderAndAttribute {
    private final String title;

    public HeaderItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}
