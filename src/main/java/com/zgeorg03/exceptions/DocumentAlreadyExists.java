package com.zgeorg03.exceptions;

public class DocumentAlreadyExists extends Exception {
    public DocumentAlreadyExists(String name){
        super("Document "+name+" already exists");
    }
}
