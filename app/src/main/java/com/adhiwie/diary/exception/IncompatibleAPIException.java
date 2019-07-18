package com.adhiwie.diary.exception;

public class IncompatibleAPIException extends Exception {

    private static final long serialVersionUID = -12347;

    public IncompatibleAPIException() {
        super("The API version is not compatible for runtime permission.");
    }

}
