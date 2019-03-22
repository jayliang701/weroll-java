package com.magicfish.weroll.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

    public static ServiceException wrapper(Exception e) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        e.printStackTrace(pout);
        String message = new String(out.toByteArray());
        pout.close();
        try {
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ServiceException(message, 0);
    }

    public void printErrorMessage() {
        System.err.format("[%s] %s", getCode(), getMessage());
    }

    public void printErrorMessage(String prefix) {
        System.err.format("%s [%s] %s", prefix, getCode(), getMessage());
    }
}
