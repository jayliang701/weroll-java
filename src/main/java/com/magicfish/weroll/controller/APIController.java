package com.magicfish.weroll.controller;

import com.magicfish.weroll.annotation.API;
import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.consts.ErrorCodes;
import com.magicfish.weroll.exception.ServiceException;
import com.magicfish.weroll.middleware.APIPermissionMiddleware;
import com.magicfish.weroll.model.APIDef;
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

    protected HashMap<String, APIDef> apis;

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
        return process(request);
    }

    protected Object process(APIAction action) {
        logger.debug("execute API: " + action.getMethod());
        Object result;
        try {
            APIPostBody postBody = action.getPostBody();
            HashMap<String, Object> postData = postBody.getData();
            String apiName = postBody.getMethod();
            APIObj api = this.getAPI(apiName);
            if (api == null || api.apiDef == null) {
                throw new ServiceException("no such method", ErrorCodes.NO_SUCH_METHOD);
            }
            Object ins = api.apiDef.getInstance();
            java.lang.reflect.Method method = api.getMethod(api.method);

            Method methodDef = api.getMethodDef(api.method);
            if (method == null || methodDef == null) {
                throw new ServiceException("no such method", ErrorCodes.NO_SUCH_METHOD);
            }

            if (!this.checkPermission(action, methodDef)) {
                throw new ServiceException("no permission", ErrorCodes.NO_PERMISSION);
            }

            Param[] paramDef = methodDef.params();
            boolean needActionArg = true;
            Object[] objs;
            Class<?>[] typeClasses = method.getParameterTypes();
            if (typeClasses.length > 0 && typeClasses[typeClasses.length - 1].equals(APIAction.class)) {
                objs = new Object[paramDef.length + 1];
            } else {
                objs = new Object[paramDef.length];
                needActionArg = false;
            }

            for (int i = 0; i < paramDef.length; i++) {
                Param param = paramDef[i];
                objs[i] = null;
                Object val;
                String name = param.name();
                if (postData.containsKey(name)) {
                    val = postData.get(name);
                } else {
                    if (param.required()) {
                        throw new ServiceException("param [" + name + "] is required", ErrorCodes.REQUEST_PARAMS_INVALID);
                    }
                    // set default value
                    val = TypeConverter.castValueAs(param.defaultValue(), param.type());
                }
                objs[i] = val;
            }
            if (needActionArg) objs[objs.length - 1] = action;

            try {
                result = action.sayOK(method.invoke(ins, objs));
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

    @Override
    protected void injectAnnotation() throws Exception {
        apis = new HashMap<>();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(API.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object ins = entry.getValue();
            Class cls = ins.getClass();
            API apiAnnotation = (API) cls.getAnnotation(API.class);
            HashMap<String, Method> methodDefMap = new HashMap<>();
            HashMap<String, java.lang.reflect.Method> methodMap = new HashMap<>();
            java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
            if (methods != null && methods.length > 0) {
                for (java.lang.reflect.Method method : methods) {
                    Method annotation = (Method) method.getAnnotation(Method.class);
                    if (annotation != null) {
                        methodDefMap.put(annotation.name(), annotation);
                        methodMap.put(annotation.name(), method);
                        logger.info("register api: " + apiAnnotation.name() + "." + annotation.name());
                    }
                }
            }

            APIDef def = new APIDef(apiAnnotation.name(), ins, methodDefMap, methodMap);
            apis.put(def.getName(), def);
        }
    }
}

class APIObj {

    public APIDef apiDef;

    public String api;

    public String method;

    public Method getMethodDef(String name) {
        return apiDef.getMethodDef(name);
    }

    public java.lang.reflect.Method getMethod(String name) {
        return apiDef.getMethod(name);
    }

}
