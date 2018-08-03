package com.magicfish.weroll.controller;

import com.magicfish.weroll.aspect.Param;
import com.magicfish.weroll.aspect.Router;
import com.magicfish.weroll.consts.ErrorCodes;
import com.magicfish.weroll.exception.ServiceException;
import com.magicfish.weroll.middleware.PagePermissionMiddleware;
import com.magicfish.weroll.net.PageAction;
import com.magicfish.weroll.utils.ClassUtil;
import com.magicfish.weroll.utils.TypeConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
public class RouterController {

    protected HashMap<String, RouterObj> routers;

    protected PagePermissionMiddleware permissionMiddleware;

    public RouterController() throws Exception {
        routers = new HashMap<>();

        permissionMiddleware = new PagePermissionMiddleware();

        findAllMethodAnnotation(ClassUtil.getClasses("com.magicfish.weroll.router"));
    }

    @GetMapping("/")
    public Object renderRootPage(HttpServletRequest request, HttpServletResponse response) {
        return "/index";
    }

    @GetMapping("/{.+}")
    public Object renderPage(Model model, HttpServletRequest request, HttpServletResponse response) throws ExecutionException, InterruptedException {
        CompletableFuture<Object> task = process(model, request, response);
        Object path = task.thenApplyAsync(result -> result).get();
        if (path == null || path.equals(404)) {
            return "/404";
        } else if (path.equals(403)) {
            return "/403";
        } else if (path.equals(500)) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return path;
    }

    @Async("request")
    protected CompletableFuture<Object> process(Model model, HttpServletRequest request, HttpServletResponse response) {
        Object path = request.getRequestURI();
        RouterObj routerObj = routers.getOrDefault(path, null);
        if (routerObj == null) {
            return CompletableFuture.completedFuture(404);
        }

        Router router = routerObj.router;

        // check permission
        PageAction action = new PageAction(request, response, router);

        if (permissionMiddleware.process(action).equals(false)) {
            return CompletableFuture.completedFuture(403);
        }

        try {
            Class<?>[] typeClasses = routerObj.method.getParameterTypes();
            Object[] objs = new Object[typeClasses.length];
            if (typeClasses.length > 0 && typeClasses[typeClasses.length - 1].equals(PageAction.class)) {
                objs[objs.length - 1] = action;
                if (typeClasses.length > 1 && typeClasses[typeClasses.length - 2].equals(Model.class)) {
                    objs[objs.length - 2] = model;
                }
            } else if (typeClasses.length > 0 && typeClasses[typeClasses.length - 1].equals(Model.class)) {
                objs[objs.length - 1] = model;
            }
            Param[] paramDef = router.params();
            for (int i = 0; i < paramDef.length; i++) {
                Param param = paramDef[i];
                objs[i] = null;
                String name = param.name();
                String val = (String) action.getParam(name);
                if (val != null && !val.isEmpty()) {
                    objs[i] = TypeConverter.castValueAs(val, param.type());
                } else {
                    if (param.required()) {
                        throw new Exception("param [" + name + "] is required");
                    }
                    // set default value
                    objs[i] = TypeConverter.castValueAs(param.defaultValue(), param.type());
                }
            }
            routerObj.method.invoke(routerObj.instance, objs);
            path = router.view();
            if (((String) path).isEmpty()) {
                path = router.path();
            }
        } catch (Exception e) {
            e.printStackTrace();
            path = 500;
        }

        return CompletableFuture.completedFuture(path);
    }

    private void findAllMethodAnnotation(Set<Class<?>> clsSet) throws Exception {
        findAllMethodAnnotation(new ArrayList<>(clsSet));
    }

    private void findAllMethodAnnotation(List<Class> clsList) throws Exception {
        if (clsList != null && clsList.size() > 0) {
            for (Class cls : clsList) {
                Object ins = null;
                Method[] methods = cls.getDeclaredMethods();
                if (methods != null && methods.length > 0) {
                    for (java.lang.reflect.Method method : methods) {
                        Router routerAnnotation = method.getAnnotation(Router.class);
                        if (routerAnnotation != null) {
                            if (ins == null) {
                                ins = cls.newInstance();
                            }
                            RouterObj routerObj = new RouterObj();
                            routerObj.instance = ins;
                            routerObj.router = routerAnnotation;
                            routerObj.method = method;
                            routers.put(routerAnnotation.path(), routerObj);
                            System.out.println("register router: " + routerAnnotation.path());
                        }
                    }
                }
            }
        }
    }
}


class RouterObj {
    public Router router;
    public Object instance;
    public Method method;
}