package com.magicfish.demo.api;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.demo.api.serializable.LocationParam;
import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.annotation.Rest;
import com.magicfish.weroll.annotation.RestGet;
import com.magicfish.weroll.annotation.RestPost;
import com.magicfish.weroll.net.HttpAction;

import java.util.List;

@Rest
public class TestAPI {

    /**
        For example
        Access url:
            http://xxxx/company/magicfish/dep/it?view=all

        You will get:
            companyId = "magicfish"
            depId = "it"
            view = "all"
            sort = "desc"   //missing in url and it's not required, so it uses defaultValue
    */
    @RestGet("/company/{companyId}/dep/{depId}")
    public Object company(String companyId,
                          String depId,
                          String view,
                          @Param(required = false, defaultValue = "desc") String sort,
                          HttpAction action) throws Exception {
        JSONObject result = new JSONObject();
        result.put("companyId", companyId);
        result.put("depId", depId);
        result.put("view", view);
        result.put("sort", sort);

        // action.isLogined()  // boolean, user logined or not
        // action.getUserPayload().getId()   // If logined, then we can get user's id. Otherwise getUserPayload() will return null.

        return result;
    }

    @RestPost(value = "/echo", needLogin = false)
    public Object echo(String name,
                       Integer age,
                       @Param(required = false, defaultValue = "[{ \"city\":\"Shanghai\", \"country\":\"China\" }]") LocationParam[] location,
                       @Param(defaultValue = "[]", required = false) String[] tags,
                       HttpAction action) throws Exception {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("age", age);
        result.put("tags", tags);
        result.put("location", location);

        return result;
    }

}
