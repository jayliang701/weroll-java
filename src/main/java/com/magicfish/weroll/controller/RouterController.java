package com.magicfish.weroll.controller;

import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.annotation.Router;
import com.magicfish.weroll.consts.ErrorCodes;
import com.magicfish.weroll.exception.ServiceException;
import com.magicfish.weroll.middleware.PagePermissionMiddleware;
import com.magicfish.weroll.net.PageAction;
import com.magicfish.weroll.utils.TypeConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
public class RouterController extends AbstractController {

    protected HashMap<String, RouterObj> routers;

    protected PagePermissionMiddleware permissionMiddleware;

    @Override
    protected String[] getInjectionPackages() {
        return new String[] {
            globalConfiguration.getRouter().getBasePackage()
        };
    }

    public RouterController() throws Exception {
        super();
    }

    @Override
    protected void initMiddleWare() {
        permissionMiddleware = new PagePermissionMiddleware();
    }

    @Override
    protected void injectAnnotation() throws Exception {
        routers = new HashMap<>();
        super.injectAnnotation();
    }

    @GetMapping("/")
    public Object renderRootPage(Model model, HttpServletRequest request, HttpServletResponse response) throws ExecutionException, InterruptedException, IOException {
        return renderPage(model, request, response);
    }

    @GetMapping("/{.+}")
    public Object renderPage(Model model, HttpServletRequest request, HttpServletResponse response) throws ExecutionException, InterruptedException, IOException {
        CompletableFuture<Object> task = process(model, request, response);
        Object path = task.thenApplyAsync(result -> result).get();
        if (path == null) {
            return globalConfiguration.getRouter().getPageNotFound();
        }
        if (Integer.class.isInstance(path) || int.class.isInstance(path)) {
            if (path.equals(404)) {
                return globalConfiguration.getRouter().getPageNotFound();
            } else if (path.equals(401)) {
                response.sendRedirect(globalConfiguration.getAuth().getEntryPoint());
                return null;
            } else if (path.equals(403)) {
                return globalConfiguration.getAuth().getDeniedRedirect();
            } else if (path.equals(500)) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return path;
    }

    protected int checkPermission(PageAction action) {
        CompletableFuture<?> task = permissionMiddleware.process(action);
        Object code = 1;
        try {
            code = task.thenApply(result -> result).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return (int) code;
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

        int checkCode = checkPermission(action);
        if (checkCode != 1) {
            return CompletableFuture.completedFuture(checkCode);
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
                        throw new ServiceException("param [" + name + "] is required", ErrorCodes.REQUEST_PARAMS_INVALID);
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
            String prefix = "Access page \"" + (String) path + "\" error: ";
            if (ServiceException.class.isInstance(e)) {
                ((ServiceException) e).printErrorMessage(prefix);
            } else {
                System.err.println(prefix);
                e.printStackTrace();
            }
            path = 500;
        }

        return CompletableFuture.completedFuture(path);
    }

    @Override
    protected void findAllMethodAnnotation(List<Class> clsList) throws Exception {
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