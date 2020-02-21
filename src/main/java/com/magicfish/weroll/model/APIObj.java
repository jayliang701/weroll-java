package com.magicfish.weroll.model;

import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.net.APIAction;

public class APIObj {

    private APIGroup group;

    private java.lang.reflect.Method method;

    private Method methodDef;

    private boolean needActionArg = true;

    private int paramsCount = 0;

    public APIObj(APIGroup group, java.lang.reflect.Method method, Method methodDef) {
        this.group = group;
        this.method = method;
        this.methodDef = methodDef;

        Param[] paramDef = methodDef.params();
        Class<?>[] typeClasses = method.getParameterTypes();
        if (typeClasses.length > 0 && typeClasses[typeClasses.length - 1].equals(APIAction.class)) {
            paramsCount = paramDef.length + 1;
        } else {
            paramsCount = paramDef.length;
            needActionArg = false;
        }
    }

    public String getFullName() {
        return group.getName() + "." + methodDef.name();
    }

    public APIGroup getGroup() {
        return group;
    }

    public java.lang.reflect.Method getMethod() {
        return method;
    }

    public Method getMethodDef() {
        return methodDef;
    }

    public Param[] getParamsDef() {
        return methodDef.params();
    }

    public boolean isNeedActionArg() {
        return needActionArg;
    }

    public int getParamsCount() {
        return paramsCount;
    }

    public Object exec(Object[] args) throws Exception{
        return method.invoke(group.getInstance(), args);
    }

}