package com.telpo.tps550.api.demo.customize.Models;

public class MyItem {
    private final String  title;
    private final String  description;
    private final int resId;
    private final int id;

    public MyItem(String title, String description, int resId, int id) {
        this.title = title;
        this.description = description;
        this.resId = resId;
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getResId() {
        return resId;
    }

    public int getId() {
        return id;
    }
}
