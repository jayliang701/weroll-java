package com.magicfish.weroll.model;

import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.exception.TypeException;
import com.magicfish.weroll.net.APIAction;
import com.magicfish.weroll.net.HttpAction;
import com.magicfish.weroll.utils.TypeConverter;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.annotation.Annotation;

public class APIObj {

    private APIGroup group;

    private java.lang.reflect.Method method;

    private Method methodDef;

    private boolean needActionArg = true;

    private int paramsCount = 0;

    private APIParamObj[] paramsDef;

    public APIObj(APIGroup group, java.lang.reflect.Method method, Method methodDef) {
        this.group = group;
        this.method = method;
        this.methodDef = methodDef;

        Class<?>[] typeClasses = method.getParameterTypes();
        if (typeClasses.length > 0 && HttpAction.class.isAssignableFrom(typeClasses[typeClasses.length - 1])) {
            needActionArg = true;
        } else {
            needActionArg = false;
        }

        paramsCount = method.getParameterCount();

        paramsDef = new APIParamObj[paramsCount];

        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] paramNames = discoverer.getParameterNames(method);

        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        if (paramAnnotations.length > 0) {
            for (int i = 0; i < paramAnnotations.length; i++) {
                APIParamObj paramObj = new APIParamObj();
                paramsDef[i] = paramObj;

                Class<?> type = typeClasses[i];

                paramObj.setType(type);

                Annotation[] annotations = paramAnnotations[i];
                if (annotations != null && annotations.length > 0) {
                    for (int j = 0; j < annotations.length; j++) {
                        Annotation annotation = annotations[j];
                        if (annotation instanceof Param) {
                            Param paramAnnotation = (Param) annotation;

                            paramObj.setAnnotation(paramAnnotation);

                            String defaultValue = paramAnnotation.defaultValue();
                            if (defaultValue != null && !defaultValue.isEmpty()) {
                                try {
                                    TypeConverter.castValueAs(defaultValue, type, paramObj.isSimpleType(), true);
                                } catch (TypeException e) {
                                    throw new RuntimeException("invalid default value was found in " + getFullName() + "(" + type.getSimpleName() + " " + paramObj.getName() + ")");
                                }
                            }

                            j = annotations.length;
                        }
                    }
                }
            }
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

    public APIParamObj[] getParamsDef() {
        return paramsDef;
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