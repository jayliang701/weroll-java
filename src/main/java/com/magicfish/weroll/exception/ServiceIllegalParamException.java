package com.magicfish.weroll.exception;

import com.magicfish.weroll.annotation.Param;

public class ServiceIllegalParamException extends Exception {
    protected Param paramDef;

    public Param getParamDef() {
        return paramDef;
    }

    protected Object val;

    public Object getVal() {
        return val;
    }

    public ServiceIllegalParamException(Param paramDef, Object val) {
        this.paramDef = paramDef;
        this.val = val;
    }

    @Override
    public String getMessage() {
        return "param [" + paramDef.name() + "] is illegal";
    }
}
