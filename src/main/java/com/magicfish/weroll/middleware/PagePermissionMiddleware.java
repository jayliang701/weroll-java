package com.magicfish.weroll.middleware;

import com.magicfish.weroll.net.PageAction;

import java.util.concurrent.CompletableFuture;

public class PagePermissionMiddleware extends AbstractPageMiddleware {

    @Override
    protected CompletableFuture<?> processPageAction(PageAction action, Object... args) {
        return CompletableFuture.supplyAsync(() -> {
            if (action.getRouter().needLogin() && !action.isLogined()) {
                return 401;
            }
            return 1;
        }).thenApply(result -> result);
    }
}
