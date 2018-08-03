package com.magicfish.weroll.middleware;

import com.magicfish.weroll.aspect.Method;
import com.magicfish.weroll.net.APIAction;

import java.util.concurrent.CompletableFuture;

public class APIPermissionMiddleware extends AbstractAPIMiddleware {

    @Override
    protected CompletableFuture<?> processAPIAction(APIAction action, Object... args) {
        return CompletableFuture.supplyAsync(() -> {
            Method methodDef = (Method) args[0];
            if (methodDef.needLogin() && !action.isLogined()) {
                return false;
            }
            return true;
        }).thenApply(result -> result);
    }
}
