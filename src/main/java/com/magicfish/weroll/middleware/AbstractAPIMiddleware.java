package com.magicfish.weroll.middleware;

import com.magicfish.weroll.net.HttpAction;

public class AbstractAPIMiddleware implements IMiddleware {
    @Override
    public Object process(HttpAction action, Object... args) {
        return null;
    }
}
