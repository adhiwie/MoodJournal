package com.adhiwie.moodjournal.exception;

public class FileNotCreatedException extends Exception {

    private static final long serialVersionUID = -12345;

    public FileNotCreatedException(String message) {
        super("FileNotCreatedException: " + message);
    }

}
