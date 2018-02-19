package com.zgeorg03.exceptions;

public class CollectionAlreadyExists extends Exception {
    public CollectionAlreadyExists(String name){
        super("Collection"+name+" already exists");
    }
}
