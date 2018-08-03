package com.magicfish.weroll.middleware;

import com.magicfish.weroll.net.APIAction;
import com.magicfish.weroll.net.HttpAction;

import java.util.concurrent.CompletableFuture;

public class AbstractAPIMiddleware implements IMiddleware {
    @Override
    public CompletableFuture<?> process(HttpAction action, Object ...args) {
        CompletableFuture<?> task = this.processAPIAction((APIAction) action, args);
        return task.thenApplyAsync(result -> result);
    }

    protected CompletableFuture<?> processAPIAction(APIAction action, Object ...args) {
        return CompletableFuture.completedFuture(null);
    }
}
