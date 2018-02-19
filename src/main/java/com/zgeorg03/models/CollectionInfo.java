package com.zgeorg03.models;

public class CollectionInfo {
    private final String name;
    private final int size;

    public CollectionInfo(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }
}
