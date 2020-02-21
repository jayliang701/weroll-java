package com.magicfish.weroll.controller;

import com.magicfish.weroll.annotation.API;
import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.consts.ErrorCodes;
import com.magicfish.weroll.exception.ServiceException;
import com.magicfish.weroll.middleware.APIPermissionMiddleware;
import com.magicfish.weroll.model.APIGroup;
import com.magicfish.weroll.model.APIObj;
import com.magicfish.weroll.model.APIPostBody;
import com.magicfish.weroll.net.APIAction;
import com.magicfish.weroll.utils.TypeConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class APIController extends AbstractController {

    protected HashMap<String, APIGroup> apiGroups;

    protected HashMap<String, APIObj> apiObjs;

    protected APIPermissionMiddleware permissionMiddleware;

    public APIController(ApplicationContext applicationContext) throws Exception {
        super(applicationContext);
    }

    @Override
    protected void initMiddleWare() {
        permissionMiddleware = new APIPermissionMiddleware();
    }

    @ResponseBody
    @PostMapping("/api")
    public Object api(@RequestBody APIPostBody body, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ExecutionException, InterruptedException {
        APIAction request = new APIAction(servletRequest, servletResponse, body);
        // logger.info("Thread Name ---> " + Thread.currentThread().getName());
        return process(request);
    }

    protected Object process(APIAction action) {
        // logger.info("execute API: " + action.getMethod());
        Object result;
        try {
            APIPostBody postBody = action.getPostBody();
            HashMap<String, Object> postData = postBody.getData();
            String apiName = postBody.getMethod();
            APIObj api = this.getAPI(apiName);
            if (api == null) {
                throw new ServiceException("no such method", ErrorCodes.NO_SUCH_METHOD);
            }

            Method methodDef = api.getMethodDef();

            if (!this.checkPermission(action, methodDef)) {
                throw new ServiceException("no permission", ErrorCodes.NO_PERMISSION);
            }

            Param[] paramDef = methodDef.params();
            Object[] args = new Object[api.getParamsCount()];

            for (int i = 0; i < paramDef.length; i++) {
                Param param = paramDef[i];
                args[i] = null;
                Object val;
                String name = param.name();
                if (postData.containsKey(name)) {
                    val = postData.get(name);
                    if (!TypeConverter.isMatchType(val, param.type())) {
                        throw new ServiceException("param [" + name + "] should be [" + param.type() + "] type", ErrorCodes.REQUEST_PARAMS_INVALID);
                    }
                    // val = TypeConverter.transferValue(val, param.type());
                } else {
                    if (param.required()) {
                        throw new ServiceException("param [" + name + "] is required", ErrorCodes.REQUEST_PARAMS_INVALID);
                    }
                    // set default value
                    val = TypeConverter.castValueAs(param.defaultValue(), param.type());
                }
                args[i] = val;
            }
            if (api.isNeedActionArg()) args[args.length - 1] = action;

            try {
                result = action.sayOK(api.exec(args));
            } catch (Exception e) {
                if (ServiceException.class.isInstance(e)) {
                    throw (ServiceException) e;
                } else if (ServiceException.class.isInstance(e.getCause())) {
                    throw (ServiceException) (e.getCause());
                } else {
                    throw ServiceException.wrapper(e);
                }
            }
        } catch (IllegalArgumentException e) {
            result = action.sayError(ErrorCodes.REQUEST_PARAMS_INVALID, "invalid request params");
        } catch (ServiceException e) {
            result = action.sayError(e);
        }
        return result;
    }

    protected boolean checkPermission(APIAction action, Method methodDef) {
        Object allow = permissionMiddleware.process(action, methodDef);
        return (boolean) allow;
    }

    public APIObj getAPI(String name) {
        APIObj obj = apiObjs.get(name);
        return obj;
    }

    @Override
    protected void injectAnnotation() throws Exception {
        apiGroups = new HashMap<>();
        apiObjs = new HashMap<>();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(API.class);
        for (Map.Entry<String, Object> beanEntry : beans.entrySet()) {
            APIGroup def = new APIGroup(beanEntry.getValue());
            apiGroups.put(def.getName(), def);

            for (Map.Entry<String, APIObj> apiObjEntry : def.getAPIObjs().entrySet()) { 
                apiObjs.put(apiObjEntry.getKey(), apiObjEntry.getValue());
                logger.info("register api: " + apiObjEntry.getKey());
            }
        }
    }
}
