package com.zgeorg03.exceptions;

public class CollectionNotFound extends Exception {
    public CollectionNotFound(String name){
        super("Collection"+name+" not found");
    }
}
