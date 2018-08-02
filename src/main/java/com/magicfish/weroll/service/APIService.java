package com.magicfish.weroll.service;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.aspect.API;
import com.magicfish.weroll.aspect.Method;
import com.magicfish.weroll.model.APIDef;
import com.magicfish.weroll.net.APIRequest;
import com.magicfish.weroll.utils.ClassUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class APIService {

    protected HashMap<String, APIDef> apis;

    public APIService() throws Exception {
        apis = new HashMap<>();

        findAllMethodAnnotation(ClassUtil.getClasses("com.magicfish.weroll.service.api"));
    }

    public Object exec(APIRequest request) {
        System.out.println("exec api...");
        String apiName = request.getPostBody().getMethod();
        APIObj api = getAPI(apiName);
        Object ins = api.apiDef.getInstance();
        JSONObject result = new JSONObject();
        try {
            java.lang.reflect.Method method = ins.getClass().getMethod(api.method, JSONObject.class, APIRequest.class);

            result.put("code", 1);
            result.put("data", method.invoke(ins, (JSONObject) (request.getPostBody().getData()), request));
        } catch (Exception e) {
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    public APIObj getAPI(String name) {
        APIObj obj = null;
        String[] sp = name.split("\\.", 2);
        if (sp == null || sp.length < 2) {
            return null;
        }
        String tmp = sp[0];
        if (tmp != null && !tmp.isEmpty()) {
            APIDef def = apis.containsKey(tmp) ? apis.get(tmp) : null;
            if (def != null) {
                obj = new APIObj();
                obj.apiDef = def;
                obj.api = sp[0];
                obj.method = sp[1];
            }
        }
        return obj;
    }

    private void findAllMethodAnnotation(Set<Class<?>> clsSet) throws Exception {
        findAllMethodAnnotation(new ArrayList<>(clsSet));
    }

    private void findAllMethodAnnotation(List<Class> clsList) throws Exception {
        if (clsList != null && clsList.size() > 0) {
            for (Class cls : clsList) {

                API apiAnnotation = (API) cls.getAnnotation(API.class);
                if (apiAnnotation != null) {
                    Object ins = cls.newInstance();
                    Set<String> methodMap = new LinkedHashSet<>();
                    java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
                    if (methods != null && methods.length > 0) {
                        for (java.lang.reflect.Method method : methods) {
                            Method annotation = (Method) method.getAnnotation(Method.class);
                            if (annotation != null) {
                                methodMap.add(annotation.name());
                                System.out.println("register api: " + apiAnnotation.name() + "." + annotation.name());
                            }
                        }
                    }

                    APIDef def = new APIDef(apiAnnotation.name(), ins, methodMap);
                    apis.put(def.getName(), def);
                }
            }
        }
    }


}

class APIObj {
    public APIDef apiDef;
    public String api;
    public String method;
}