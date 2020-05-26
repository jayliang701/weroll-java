package com.magicfish.weroll.utils;

import java.util.HashMap;
import java.util.Map;

public class ClassNameLoader extends ClassLoader {

    private Map<String, String> names;

    public ClassNameLoader() {
        names = new HashMap<>();
    }

    public String[] getClassNames() {
        int i = 0;
        String[] list = new String[names.size()];
        for(Map.Entry<String, String> entry : names.entrySet()){
            list[i] = entry.getValue();
            i ++;
        }
        return list;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        names.put(name, name);
        return Object.class;
    }
}
