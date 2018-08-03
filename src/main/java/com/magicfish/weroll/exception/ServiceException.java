package com.magicfish.weroll.exception;

public class ServiceException extends Error {
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
}
