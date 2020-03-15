package com.magicfish.weroll.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.magicfish.weroll.consts.ErrorCodes;

public class ServiceException extends Exception {
    
    private static final long serialVersionUID = -3637587713268375921L;

    private int code = ErrorCodes.SERVER_ERROR;

    public int getCode() {
        return code;
    }

    public ServiceException() {
        super("unknown");
        this.code = ErrorCodes.SERVER_ERROR;
    }

    public ServiceException(String message) {
        super(message);
        this.code = ErrorCodes.SERVER_ERROR;
    }

    public ServiceException(String message, int code) {
        super(message);
        this.code = code;
    }

    public static ServiceException wrapper(Exception e) {
        // ByteArrayOutputStream out = new ByteArrayOutputStream();
        // PrintStream pout = new PrintStream(out);
        // e.printStackTrace(pout);
        // String message = new String(out.toByteArray());
        // pout.close();
        // try {
        //     out.close();
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }
        return new ServiceException(e.getCause().getMessage(), ErrorCodes.SERVER_ERROR);
    }

    public void printErrorMessage() {
        System.err.format("[%s] %s", getCode(), getMessage());
    }

    public void printErrorMessage(String prefix) {
        System.err.format("%s [%s] %s", prefix, getCode(), getMessage());
    }
}
