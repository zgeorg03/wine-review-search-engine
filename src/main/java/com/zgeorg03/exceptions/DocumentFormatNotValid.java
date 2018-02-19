package com.zgeorg03.exceptions;

public class DocumentFormatNotValid extends Exception {
    public DocumentFormatNotValid(String name){
        super("Format of document "+name+" is not valid");
    }
}
