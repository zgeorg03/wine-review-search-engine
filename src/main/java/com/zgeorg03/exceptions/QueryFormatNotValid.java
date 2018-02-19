package com.zgeorg03.exceptions;

public class QueryFormatNotValid extends Exception {
    public QueryFormatNotValid(String query){
        super("Format of the query "+query+" is not valid");
    }
}
