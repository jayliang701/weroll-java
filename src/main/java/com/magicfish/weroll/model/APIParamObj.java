package com.magicfish.weroll.model;

import com.magicfish.weroll.annotation.Param;
import org.springframework.beans.BeanUtils;

public class APIParamObj {

    private Class<?> type;

    public String getName() {
        if (annotation == null) return null;
        return annotation.name();
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
        isSimpleType = BeanUtils.isSimpleValueType(type);
    }

    private boolean isSimpleType = true;

    public boolean isSimpleType() {
        return isSimpleType;
    }

    public String getDefaultValue() {
        if (annotation == null) return null;
        return annotation.defaultValue();
    }

    public Boolean hasDefaultValue() {
        Object defaultValue = getDefaultValue();
        return defaultValue != null;
    }

    public Boolean getRequired() {
        if (annotation == null) return false;
        return annotation.required();
    }

    private Param annotation;

    public Param getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Param annotation) {
        this.annotation = annotation;
    }
}
