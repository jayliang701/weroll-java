package com.magicfish.weroll.service.api;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.aspect.API;
import com.magicfish.weroll.aspect.Method;
import com.magicfish.weroll.aspect.Param;
import com.magicfish.weroll.exception.ServiceException;
import com.magicfish.weroll.net.APIAction;

import java.util.Date;
import java.util.UUID;

@API(name = "system")
public class SystemAPI {

    @Method(name = "ping",
            params = {
                @Param(name = "name", type = "string"),
                @Param(name = "gender", type = "int", defaultValue = "1")
            })
    public Object ping(String name, int gender, APIAction request) throws ServiceException {
        JSONObject result = new JSONObject();
        result.put("ip", request.getRemoteClientIP());
        result.put("time", new Date().toString());
        result.put("name", name);
        result.put("gender", gender);
        return result;
    }

    @Method(name = "uuid",
            params = {
                @Param(name = "name", type = "string", required = false)
            })
    public Object uuid(String name) throws ServiceException {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("uuid", UUID.randomUUID());
        return result;
    }

    @Method(name = "test",
            needLogin = true,
            params = {
                @Param(name = "name", type = "string", required = false)
            })
    public Object test(String name) throws ServiceException {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("uuid", UUID.randomUUID());
        return result;
    }

}
