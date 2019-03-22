package com.magicfish.weroll.middleware;

import com.magicfish.weroll.net.HttpAction;

public interface IMiddleware {

    Object process(HttpAction action, Object... args);

}
