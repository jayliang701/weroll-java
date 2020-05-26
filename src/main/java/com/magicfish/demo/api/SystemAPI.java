package com.magicfish.demo.api;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.demo.api.serializable.LocationParam;
import com.magicfish.weroll.annotation.API;
import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.exception.ServiceException;
import com.magicfish.weroll.net.APIAction;
import com.magicfish.weroll.net.HttpAction;

import java.util.List;

@API(name = "system")
public class SystemAPI {

//    @Method(name = "ping",
//            params = {
//                    @Param(name = "name", type = "string"),
//                    @Param(name = "gender", type = "int", defaultValue = "1", required = false),
//                    @Param(name = "isAdmin", type = "boolean", defaultValue = "0", required = false),
//                    @Param(name = "address", type = "object", defaultValue = "{ city:\"shanghai\", street:\"unknown\" }", required = false),
//                    @Param(name = "tags", type = "array", defaultValue = "[]", required = false)
//            })
//    public Object ping(String name, int gender, boolean isAdmin, HashMap<String, String> address, ArrayList<String> tags, APIAction request) throws ServiceException {
//        JSONObject result = new JSONObject();
//        result.put("ip", request.getRemoteClientIP());
//        result.put("time", new Date().toString());
//        result.put("name", name);
//        result.put("gender", gender);
//        result.put("isAdmin", isAdmin);
//        result.put("address_city", address.get("city"));
//        result.put("address_street", address.get("street"));
//        result.put("tags", tags);
//        return result;
//    }
//
//    @Method(name = "uuid",
//            params = {
//                    @Param(name = "name", type = "string", required = false)
//            })
//    public Object uuid(String name) throws ServiceException {
//        JSONObject result = new JSONObject();
//        result.put("name", name);
//        result.put("uuid", UUID.randomUUID());
//        return result;
//    }

    @Method(needLogin = false)
    public Object echo(@Param(defaultValue = "guest", required = false) String name,
                       Integer age,
                       @Param(defaultValue = "true", required = false) boolean isStudent,
                       @Param(defaultValue = "{ \"city\":\"Shanghai\", \"country\":\"China\" }") LocationParam location,
                       @Param(defaultValue = "[]", required = false) String[] tags,
                       APIAction action) throws ServiceException {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("age", age);
        result.put("tags", tags);
        result.put("location", location);

        // action.getPostBody().getData()     // Get full post data

        return result;
    }

}
