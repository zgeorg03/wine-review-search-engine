package com.zgeorg03.exceptions;

public class NoPermissionsException extends Exception {
    public NoPermissionsException(String name){
        super("No permission on"+name);
    }
}
