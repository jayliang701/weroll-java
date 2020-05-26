package com.magicfish.weroll.controller;

import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.annotation.Router;
import com.magicfish.weroll.config.ViewTemplateConfiguration;
import com.magicfish.weroll.exception.TypeException;
import com.magicfish.weroll.middleware.PagePermissionMiddleware;
import com.magicfish.weroll.model.APIParamObj;
import com.magicfish.weroll.net.PageAction;
import com.magicfish.weroll.utils.TypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class RouterProcessor extends AbstractHttpProcessor {

    @Autowired
    private ViewTemplateConfiguration viewTemplateConfiguration;

    protected HashMap<String, RouterObj> routers;

    protected PagePermissionMiddleware permissionMiddleware;

    public RouterProcessor(ApplicationContext applicationContext) throws Exception {
        super(applicationContext);
    }

    @Override
    protected void initMiddleWare() {
        permissionMiddleware = new PagePermissionMiddleware();
    }

    protected int checkPermission(PageAction action) {
        Object code = permissionMiddleware.process(action);
        return (int) code;
    }

    @Override
    public Object process(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.addAttribute("setting", viewTemplateConfiguration);

        Object path = request.getRequestURI();
//        RouterObj routerObj = routers.getOrDefault(path, null);
//        if (routerObj == null) {
//            return 404;
//        }
//
//        Router router = routerObj.router;
//
//        // check permission
//        PageAction action = new PageAction(request, response, router);
//
//        int checkCode = checkPermission(action);
//        if (checkCode != 1) {
//            return checkCode;
//        }
//
//        try {
//            Class<?>[] typeClasses = routerObj.typeClasses;
//            int typeClassesCount = typeClasses.length;
//            Object[] objs = new Object[typeClassesCount];
//            if (typeClassesCount > 0 && typeClasses[typeClassesCount - 1].equals(PageAction.class)) {
//                objs[objs.length - 1] = action;
//                if (typeClassesCount > 1 && typeClasses[typeClassesCount - 2].equals(Model.class)) {
//                    objs[objs.length - 2] = model;
//                }
//            } else if (typeClassesCount > 0 && typeClasses[typeClassesCount - 1].equals(Model.class)) {
//                objs[objs.length - 1] = model;
//            }
//            Param[] paramDef = router.params();
//
//            for (int i = 0; i < paramDef.length; i++) {
//                Param param = paramDef[i];
//                objs[i] = null;
//                String name = param.name();
//                String val = (String) action.getParam(name);
//                if (val != null && !val.isEmpty()) {
//                    objs[i] = TypeConverter.castValueAs(val, param.type());
//                } else {
//                    if (param.required()) {
//                        throw new ServiceException("param [" + name + "] is required", ErrorCodes.REQUEST_PARAMS_INVALID);
//                    }
//                    // set default value
//                    objs[i] = TypeConverter.castValueAs(param.defaultValue(), param.type());
//                }
//            }
//            routerObj.method.invoke(routerObj.instance, objs);
//            path = router.view();
//            if (((String) path).isEmpty()) {
//                path = router.path();
//            }
//        } catch (Exception e) {
//            String prefix = "Access page \"" + (String) path + "\" error: ";
//            if (ServiceException.class.isInstance(e)) {
//                ((ServiceException) e).printErrorMessage(prefix);
//            } else {
//                System.err.println(prefix);
//                e.printStackTrace();
//            }
//            path = 500;
//        }

        if (path == null) {
            return globalSetting.getRouter().getPageNotFound();
        }
        if (Integer.class.isInstance(path) || int.class.isInstance(path)) {
            if (path.equals(404)) {
                return globalSetting.getRouter().getPageNotFound();
            } else if (path.equals(401)) {
                response.sendRedirect(globalSetting.getAuth().getEntryPoint());
                return null;
            } else if (path.equals(403)) {
                return globalSetting.getAuth().getDeniedRedirect();
            } else if (path.equals(500)) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return path;
    }

    @Override
    protected void injectAnnotation() throws Exception {
//        routers = new HashMap<>();
//        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RouterGroup.class);
//
//        for (Map.Entry<String, Object> entry : beans.entrySet()) {
//            Object ins = entry.getValue();
//            Class cls = ins.getClass();
//            Method[] methods = cls.getDeclaredMethods();
//            if (methods != null && methods.length > 0) {
//                for (java.lang.reflect.Method method : methods) {
//                    Router routerAnnotation = method.getAnnotation(Router.class);
//                    if (routerAnnotation != null) {
//                        RouterObj routerObj = new RouterObj(method);
//                        routerObj.instance = ins;
//                        routerObj.router = routerAnnotation;
//                        routerObj.method = method;
//                        routerObj.typeClasses = method.getParameterTypes();
//                        routers.put(routerAnnotation.path(), routerObj);
//                        logger.info("register router: " + routerAnnotation.path());
//                    }
//                }
//            }
//        }
    }
}


class RouterObj {
    public Router router;
    public Object instance;
    public Method method;
    public Class<?>[] typeClasses;

    private APIParamObj[] paramsDef;

    public RouterObj(Method method, Method methodDef) {

        paramsDef = new APIParamObj[method.getParameterCount()];

        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] paramNames = discoverer.getParameterNames(method);

        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        if (paramAnnotations.length > 0) {
            for (int i = 0; i < paramAnnotations.length; i++) {
                APIParamObj paramObj = new APIParamObj();
                paramsDef[i] = paramObj;

                Class<?> type = typeClasses[i];

                paramObj.setType(type);

                Annotation[] annotations = paramAnnotations[i];
                if (annotations != null && annotations.length > 0) {
                    for (int j = 0; j < annotations.length; j++) {
                        Annotation annotation = annotations[j];
                        if (annotation instanceof Param) {
                            Param paramAnnotation = (Param) annotation;

                            paramObj.setAnnotation(paramAnnotation);

                            String defaultValue = paramAnnotation.defaultValue();
                            if (defaultValue != null && !defaultValue.isEmpty()) {
                                try {
                                    TypeConverter.castValueAs(defaultValue, type, paramObj.isSimpleType(), true);
                                } catch (TypeException e) {
                                    throw new RuntimeException("invalid default value was found in " + methodDef.getName() + "(" + type.getSimpleName() + " " + paramObj.getName() + ")");
                                }
                            }

                            j = annotations.length;
                        }
                    }
                }
            }
        }
    }

    public APIParamObj[] getParamsDef() {
        return paramsDef;
    }
}