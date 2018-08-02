package com.magicfish.weroll.model;

import com.magicfish.weroll.aspect.Method;

import java.util.HashMap;

public class APIDef {
    private String name;

    public String getName() {
        return name;
    }

    private Object instance;

    public Object getInstance() {
        return instance;
    }

    private HashMap<String, Method> methodDefs;

    public HashMap<String, Method> getMethodDefs() {
        return methodDefs;
    }

    private HashMap<String, java.lang.reflect.Method> methods;

    public HashMap<String, java.lang.reflect.Method> getMethods() {
        return methods;
    }

    public APIDef(String name, Object instance, HashMap<String, Method> methodDefs, HashMap<String, java.lang.reflect.Method> methods) {
        this.name = name;
        this.instance = instance;
        this.methodDefs = methodDefs;
        this.methods = methods;
    }

    public Method getMethodDef(String name) {
        return methodDefs.get(name);
    }

    public java.lang.reflect.Method getMethod(String name) {
        return methods.get(name);
    }
}
