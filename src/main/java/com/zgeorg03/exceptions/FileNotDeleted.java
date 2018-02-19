package com.zgeorg03.exceptions;

public class FileNotDeleted extends Exception {
    public FileNotDeleted(String name){
        super("Couldn't delete file"+name);
    }
}
