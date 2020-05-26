package com.magicfish.demo.middleware;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.net.IRequestBodyFilter;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

public class RequestBodyDecrypt implements IRequestBodyFilter {

    @Override
    public JSONObject doFilter(HttpServletRequest request) throws Exception {
        byte[] bytes = IOUtils.toByteArray(request.getReader(), StandardCharsets.UTF_8);
        String data = new String(bytes);
        return (JSONObject) JSONObject.parse(data);
    }
}
