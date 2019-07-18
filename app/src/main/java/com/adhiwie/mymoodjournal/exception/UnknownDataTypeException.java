package com.adhiwie.mymoodjournal.exception;

public class UnknownDataTypeException extends Exception {

    private static final long serialVersionUID = -12346;

    public UnknownDataTypeException(String s) {
        super("Could not find this type of data class -- " + s);
    }

}
