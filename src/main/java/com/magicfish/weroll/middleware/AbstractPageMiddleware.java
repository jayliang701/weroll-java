package com.magicfish.weroll.middleware;

import com.magicfish.weroll.net.PageAction;
import com.magicfish.weroll.net.HttpAction;

import java.util.concurrent.CompletableFuture;

public class AbstractPageMiddleware implements IMiddleware {
    @Override
    public CompletableFuture<?> process(HttpAction action, Object ...args) {
        CompletableFuture<?> task = this.processPageAction((PageAction) action, args);
        return task.thenApplyAsync(result -> result);
    }

    protected CompletableFuture<?> processPageAction(PageAction action, Object ...args) {
        return CompletableFuture.completedFuture(null);
    }
}
