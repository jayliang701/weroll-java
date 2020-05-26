package com.magicfish.weroll.net;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

public class JSONRequestBodyFilter implements IRequestBodyFilter {
    @Override
    public JSONObject doFilter(HttpServletRequest request) throws Exception {
        byte[] bytes = IOUtils.toByteArray(request.getReader(), StandardCharsets.UTF_8);
        return (JSONObject) JSONObject.parse(bytes);
    }
}
