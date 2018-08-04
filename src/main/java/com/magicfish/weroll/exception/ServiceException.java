package com.magicfish.weroll.exception;

public class ServiceException extends Exception {
    private int code = 0;

    public int getCode() {
        return code;
    }

    public ServiceException() {
        super("unknown");
        this.code = 0;
    }

    public ServiceException(String message) {
        super(message);
        this.code = 0;
    }

    public ServiceException(String message, int code) {
        super(message);
        this.code = code;
    }

    public void printErrorMessage() {
        System.err.format("[%s] %s", getCode(), getMessage());
    }

    public void printErrorMessage(String prefix) {
        System.err.format("%s [%s] %s", prefix, getCode(), getMessage());
    }
}
