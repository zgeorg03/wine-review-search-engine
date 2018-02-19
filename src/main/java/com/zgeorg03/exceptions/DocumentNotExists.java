package com.zgeorg03.exceptions;

public class DocumentNotExists extends Exception {
    public DocumentNotExists(String name){
        super("Document "+name+" not exists");
    }
}
