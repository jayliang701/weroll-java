package com.magicfish.weroll.exception;

public class TypeException extends Exception {

    protected String typeName;

    public String getTypeName() {
        return typeName;
    }

    public TypeException(String typeName) {
        super("unsupported type [" + typeName + "]");
    }
}
