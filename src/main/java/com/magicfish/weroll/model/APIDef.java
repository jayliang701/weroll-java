package com.magicfish.weroll.model;

import java.util.Set;

public class APIDef {
    private String name;

    public String getName() {
        return name;
    }

    private Object instance;

    public Object getInstance() {
        return instance;
    }

    private Set<String> methods;

    public Set<String> getMethods() {
        return methods;
    }

    public APIDef(String name, Object instance, Set<String> methods) {
        this.name = name;
        this.instance = instance;
        this.methods = methods;
    }
}
