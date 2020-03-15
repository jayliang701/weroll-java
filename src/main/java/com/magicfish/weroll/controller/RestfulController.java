package com.magicfish.weroll.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.annotation.Rest;
import com.magicfish.weroll.middleware.APIPermissionMiddleware;
import com.magicfish.weroll.model.APIGroup;
import com.magicfish.weroll.model.APIObj;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RestfulController extends AbstractController {

    protected HashMap<String, APIGroup> apiGroups;

    protected HashMap<String, APIObj> apiObjs;

    protected APIPermissionMiddleware permissionMiddleware;

    public RestfulController(ApplicationContext applicationContext) throws Exception {
        super(applicationContext);
    }

    @Override
    protected void initMiddleWare() {
        permissionMiddleware = new APIPermissionMiddleware();
    }

    // @GetMapping(value = "/api/**")
    // public Object rest(@RequestParam Map<String,String> params, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ExecutionException, InterruptedException {
    //     // APIAction request = new APIAction(servletRequest, servletResponse, body);
    //     logger.info(servletRequest.getContextPath());
    //     logger.info(servletRequest.getServletPath());
    //     logger.info(servletRequest.getRequestURI());
    //     logger.info(params.toString());
    //     return null;
    // }

    public APIObj getAPI(String name) {
        APIObj obj = apiObjs.get(name);
        return obj;
    }

    @Override
    protected void injectAnnotation() throws Exception {
        apiGroups = new HashMap<>();
        apiObjs = new HashMap<>();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Rest.class);
        for (Map.Entry<String, Object> beanEntry : beans.entrySet()) {
            logger.info(beanEntry.getKey());
            // APIGroup def = new APIGroup(beanEntry.getValue());
            // apiGroups.put(def.getName(), def);
            Object apiInstance = beanEntry.getValue();
            Class cls = apiInstance.getClass();
            Rest restAnnotation = (Rest) cls.getAnnotation(Rest.class);
            HashMap<String, Method> methodDefMap = new HashMap<>();
            HashMap<String, java.lang.reflect.Method> methodMap = new HashMap<>();
            java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
            if (methods != null && methods.length > 0) {
                for (java.lang.reflect.Method method : methods) {
                    Method annotation = (Method) method.getAnnotation(Method.class);
                    if (annotation != null) {
                        String annotationName = annotation.name();
                        ArrayList<String> params = new ArrayList<>();
                        if (annotationName.startsWith(":")) {
                            //dynamic URI
                            String paramName = annotationName.substring(1, annotationName.length());
                            logger.info(paramName);
                            params.add(paramName);
                        }
                        methodDefMap.put(annotationName, annotation);
                        methodMap.put(annotationName, method);
                        String fullNameOfMethod = "/api/" + restAnnotation.name() + "/" + annotationName;

                        // APIObj obj = new APIObj(this, method, annotation);

                        // apiObjs.put(fullNameOfMethod, obj);
                        logger.info("register rest api: " + fullNameOfMethod);
                    }
                }
            }

            // for (Map.Entry<String, APIObj> apiObjEntry : def.getAPIObjs().entrySet()) { 
            //     apiObjs.put(apiObjEntry.getKey(), apiObjEntry.getValue());
            //     logger.info("register api: " + apiObjEntry.getKey());
            // }
        }
    }
}

class UriParamDef {
    public String name;
    public int index;

    public String toString() {
        return name + "   index: " + index;
    }
}