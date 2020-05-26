package com.magicfish.weroll.controller;

import com.magicfish.weroll.annotation.API;
import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.consts.ErrorCodes;
import com.magicfish.weroll.exception.ServiceException;
import com.magicfish.weroll.exception.ServiceIllegalParamException;
import com.magicfish.weroll.middleware.APIPermissionMiddleware;
import com.magicfish.weroll.model.APIGroup;
import com.magicfish.weroll.model.APIObj;
import com.magicfish.weroll.model.APIParamObj;
import com.magicfish.weroll.model.APIPostBody;
import com.magicfish.weroll.net.APIAction;
import com.magicfish.weroll.utils.ParamProcessor;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class APIProcessor extends AbstractHttpProcessor {

    protected HashMap<String, APIGroup> apiGroups;

    protected HashMap<String, APIObj> apiObjs;

    protected APIPermissionMiddleware permissionMiddleware;

    public APIProcessor(ApplicationContext applicationContext) throws Exception {
        super(applicationContext);
    }

    @Override
    protected void initMiddleWare() {
        permissionMiddleware = new APIPermissionMiddleware();
    }

    public Object process(APIAction action) {
        // logger.info("execute API: " + action.getMethod());
        Object result;
        try {
            APIPostBody postBody = action.getPostBody();
            Map<String, Object> postData = postBody.getData();
            String apiName = postBody.getMethod();
            APIObj api = this.getAPI(apiName);
            if (api == null) {
                throw new ServiceException("no such method", ErrorCodes.NO_SUCH_METHOD);
            }

            Method methodDef = api.getMethodDef();

            if (!this.checkPermission(action, methodDef)) {
                throw new ServiceException("no permission", ErrorCodes.NO_PERMISSION);
            }

            APIParamObj[] paramsDef = api.getParamsDef();

            try {

                Object[] args = ParamProcessor.checkAndReturnArray(paramsDef, postData, api.getParamsCount());
                if (api.isNeedActionArg()) args[args.length - 1] = action;

                result = action.sayOK(api.exec(args));
            } catch (ServiceException e) {
                throw e;
            } catch (ServiceIllegalParamException e) {
                throw new ServiceException(e.getMessage(), ErrorCodes.REQUEST_PARAMS_INVALID);
            } catch (Exception e) {
                throw ServiceException.wrapper(e);
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
