package com.magicfish.weroll.model;

import java.util.HashMap;

import com.magicfish.weroll.annotation.API;
import com.magicfish.weroll.annotation.Method;

public class APIGroup {

    public APIGroup(Object apiInstance) {
        this.instance = apiInstance;

        apiObjs = new HashMap<>();

        Class cls = apiInstance.getClass();
        API apiAnnotation = (API) cls.getAnnotation(API.class);
        this.name = apiAnnotation.name();

        HashMap<String, Method> methodDefMap = new HashMap<>();
        HashMap<String, java.lang.reflect.Method> methodMap = new HashMap<>();
        java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (java.lang.reflect.Method method : methods) {
                Method annotation = method.getAnnotation(Method.class);
                if (annotation != null) {
                    String name = annotation.name();
                    if (name.isEmpty()) {
                        name = method.getName();
                    }
                    methodDefMap.put(name, annotation);
                    methodMap.put(name, method);
                    String fullNameOfMethod = apiAnnotation.name() + "." + name;

                    APIObj obj = new APIObj(this, method, annotation);

                    apiObjs.put(fullNameOfMethod, obj);
                }
            }
        }
    }

    private String name;

    public String getName() {
        return name;
    }

    private Object instance;

    public Object getInstance() {
        return instance;
    }

    private HashMap<String, APIObj> apiObjs;

    public HashMap<String, APIObj> getAPIObjs() {
        return apiObjs;
    }

    public APIObj getAPIObj(String method) {
        return apiObjs.get(method);
    }
    /*
    private HashMap<String, Method> methodDefs;

    public HashMap<String, Method> getMethodDefs() {
        return methodDefs;
    }

    private HashMap<String, java.lang.reflect.Method> methods;

    public HashMap<String, java.lang.reflect.Method> getMethods() {
        return methods;
    }

    public Method getMethodDef(String name) {
        return methodDefs.get(name);
    }

    public java.lang.reflect.Method getMethod(String name) {
        return methods.get(name);
    }
    */
}
