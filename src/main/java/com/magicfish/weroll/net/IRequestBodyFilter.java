package com.magicfish.weroll.net;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

public interface IRequestBodyFilter {
    public JSONObject doFilter(HttpServletRequest request) throws Exception;
}
