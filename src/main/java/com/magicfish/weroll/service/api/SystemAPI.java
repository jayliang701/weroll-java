package com.magicfish.weroll.service.api;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.aspect.API;
import com.magicfish.weroll.aspect.Method;
import com.magicfish.weroll.aspect.Param;
import com.magicfish.weroll.net.APIRequest;

import java.util.Date;

@API(name = "system")
public class SystemAPI {

    @Method(
            name = "ping",
            params = {
                @Param(name = "name", type = "string"),
                @Param(name = "gender", type = "int", defaultValue = "1")
            }
    )
    public Object ping(String name, int gender, APIRequest request) {
        JSONObject result = new JSONObject();
        result.put("ip", request.getRemoteClientIP());
        result.put("time", new Date().toString());
        result.put("name", name);
        result.put("gender", gender);
        return result;
    }

}
