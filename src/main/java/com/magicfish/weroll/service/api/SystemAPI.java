package com.magicfish.weroll.service.api;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.aspect.API;
import com.magicfish.weroll.aspect.Method;
import com.magicfish.weroll.net.APIRequest;

import java.util.Date;

@API(name = "system")
public class SystemAPI {

    @Method(name = "ping")
    public Object ping(JSONObject params, APIRequest request) {
        JSONObject result = new JSONObject();
        result.put("ip", request.getRemoteClientIP());
        result.put("time", new Date().toString());
        return result;
    }

}
