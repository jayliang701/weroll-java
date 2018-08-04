package com.magicfish.weroll.net;


import com.magicfish.weroll.annotation.Router;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageAction extends HttpAction {

    protected Router router;

    public Router getRouter() {
        return router;
    }

    public PageAction(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Router router) {
        super(servletRequest, servletResponse);
        this.router = router;
    }

    public String getPath() {
        return servletRequest.getRequestURI();
    }

    public String getQueryString() {
        return servletRequest.getQueryString();
    }
}
