package com.magicfish.weroll.exception;

import com.magicfish.weroll.model.APIParamObj;

public class ServiceInvalidParamTypeException extends ServiceIllegalParamException {

    public ServiceInvalidParamTypeException(APIParamObj paramObj, Object val) {
        super(paramObj.getAnnotation(), val);
        this.paramObj = paramObj;
    }

    private APIParamObj paramObj;

    @Override
    public String getMessage() {
        return "param [" + paramObj.getName() + "] should be [" + paramObj.getType() + "] type";
    }
}
