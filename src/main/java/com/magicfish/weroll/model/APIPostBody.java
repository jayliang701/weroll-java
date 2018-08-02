package com.magicfish.weroll.model;

import java.util.HashMap;

public class APIPostBody {

    private String method;

    private HashMap<String, Object> data;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

}
