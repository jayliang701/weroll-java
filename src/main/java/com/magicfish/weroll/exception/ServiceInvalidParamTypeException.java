package com.magicfish.weroll.exception;

import com.magicfish.weroll.annotation.Param;

public class ServiceInvalidParamTypeException extends ServiceIllegalParamException {

    public ServiceInvalidParamTypeException(Param paramDef, Object val) {
        super(paramDef, val);
    }

    @Override
    public String getMessage() {
        return "param [" + paramDef.name() + "] should be [" + paramDef.type() + "] type";
    }
}
