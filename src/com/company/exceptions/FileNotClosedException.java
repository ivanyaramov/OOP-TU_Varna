package com.company.exceptions;

public class FileNotClosedException extends Exception{
    public FileNotClosedException(String message) {
        super(message);
    }
}
