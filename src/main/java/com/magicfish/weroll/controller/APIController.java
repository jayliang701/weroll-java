package com.magicfish.weroll.controller;

import com.google.common.collect.Lists;
import com.magicfish.weroll.aspect.Method;
import com.magicfish.weroll.model.APIPostBody;
import com.magicfish.weroll.net.APIRequest;
import com.magicfish.weroll.service.APIService;
import com.magicfish.weroll.service.api.SystemAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class APIController {

    public APIController() {
        findAllMethodAnnotation(Lists.newArrayList(SystemAPI.class));
    }

    @Autowired
    private APIService service;

    @ResponseBody
    @PostMapping("/api")
    public Object api(@RequestBody APIPostBody body, HttpServletRequest servletRequest) {
        APIRequest request = new APIRequest(servletRequest, body);
        return service.exec(request);
    }

    private void findAllMethodAnnotation(List<Class<?>> clsList){
        if (clsList != null && clsList.size() > 0) {
            for (Class<?> cls : clsList) {
                //获取类中的所有的方法
                java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
                if (methods != null && methods.length > 0) {
                    for (java.lang.reflect.Method method : methods) {
                        Method annotion = (Method) method.getAnnotation(Method.class);
                        if (annotion != null) {
                            System.out.println(annotion.name());
                        }
                    }
                }
            }
        }
    }
}
