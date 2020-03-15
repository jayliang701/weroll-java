package com.magicfish.demo.api;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.annotation.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class TestRest {

    @GetMapping(value = "/user/{id}")
    @Method(params = {
            @Param(name = "name", type = "string", required = false, defaultValue = "guest")
    })
    public Object info(@RequestParam Map<String, Object> params, @PathVariable String id, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("name", params.get("name"));

        return result;
    }

    @GetMapping(value = "/company/{companyId}/dep/{depId}")
    public Object info(@PathVariable String companyId, @PathVariable String depId) throws Exception {
        JSONObject result = new JSONObject();
        result.put("companyId", companyId);
        result.put("depId", depId);

        return result;
    }

}
