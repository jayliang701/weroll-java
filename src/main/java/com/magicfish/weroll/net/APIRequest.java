package com.magicfish.weroll.net;


import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.model.APIPostBody;

import javax.servlet.http.HttpServletRequest;

public class APIRequest {

    private HttpServletRequest servletRequest;

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    private long time = 0;

    private APIPostBody postBody;

    public APIPostBody getPostBody() {
        return postBody;
    }

    public String getMethod() {
        return postBody.getMethod();
    }

    public JSONObject getParams() {
        return (JSONObject) postBody.getData();
    }

    public Object getParam(String key) {
        return this.getParams().get(key);
    }

    public APIRequest(HttpServletRequest servletRequest, APIPostBody postBody) {
        this.servletRequest = servletRequest;
        this.postBody = postBody;
        this.time = System.currentTimeMillis();
    }

    public long recordTime() {
        long current = System.currentTimeMillis();
        long passed = current - time;
        time = current;
        return passed;
    }

    public String getRemoteClientIP() {
        String ip = servletRequest.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) {
            ip = servletRequest.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty()) {
            ip = servletRequest.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.isEmpty()) {
            ip = servletRequest.getRemoteAddr();
        }
        return ip;
    }
}
