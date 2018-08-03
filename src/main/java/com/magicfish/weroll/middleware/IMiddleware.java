package com.magicfish.weroll.middleware;

import com.magicfish.weroll.net.HttpAction;

import java.util.concurrent.CompletableFuture;

public interface IMiddleware {

    CompletableFuture<?> process(HttpAction action, Object ...args);

}
