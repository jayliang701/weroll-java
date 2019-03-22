package com.magicfish.weroll.middleware;

import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.net.HttpAction;

public class APIPermissionMiddleware extends AbstractAPIMiddleware {

    @Override
    public Object process(HttpAction action, Object... args) {
        Method methodDef = (Method) args[0];
        if (methodDef.needLogin() && !action.isLogined()) {
            return false;
        }
        return true;
    }
}
