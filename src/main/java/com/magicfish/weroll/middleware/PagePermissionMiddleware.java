package com.magicfish.weroll.middleware;

import com.magicfish.weroll.net.HttpAction;
import com.magicfish.weroll.net.PageAction;

public class PagePermissionMiddleware extends AbstractPageMiddleware {

    @Override
    public Object process(HttpAction action, Object... args) {
        PageAction pageAction = (PageAction) action;
        if (pageAction.getRouter().needLogin() && !action.isLogined()) {
            return 401;
        }
        return 1;
    }
}
