package com.magicfish.weroll.exception;

/**
 * Created by Jay on 2019/3/16.
 */
public class IllegalSessionTokenException extends Exception {
    public IllegalSessionTokenException() {
        super("Expired or invalid JWT token");
    }
}
