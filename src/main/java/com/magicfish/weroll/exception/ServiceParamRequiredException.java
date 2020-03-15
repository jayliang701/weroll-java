package com.magicfish.weroll.exception;

import com.magicfish.weroll.annotation.Param;

public class ServiceParamRequiredException extends ServiceIllegalParamException {

    public ServiceParamRequiredException(Param paramDef) {
        super(paramDef, null);
    }

    @Override
    public String getMessage() {
        return "param [" + paramDef.name() + "] is required";
    }
}
