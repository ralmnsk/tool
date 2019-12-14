package com.github.ralmnsk.exception.handler;

public class ParameterException extends Exception {
    public ParameterException() {
    }
    public ParameterException(String message, Throwable exception) {
        super(message, exception);
    }
    public ParameterException(String message) {
        super(message);
    }
    public ParameterException (Throwable exception) {
        super(exception);
    }
}
