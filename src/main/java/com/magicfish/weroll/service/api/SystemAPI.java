package com.magicfish.weroll.service.api;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.aspect.Method;
import com.magicfish.weroll.net.APIRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SystemAPI {

    @Method(name = "system.hello")
    public Object hello(JSONObject params, APIRequest request) {
        return params;
    }

}
