package com.magicfish.weroll.net;

import com.magicfish.weroll.model.UserAuth;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

public class HttpAction {

    protected HttpServletRequest servletRequest;

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    protected HttpServletResponse servletResponse;

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    protected long time = 0;

    public Map<String, ?> getParams() {
        return servletRequest.getParameterMap();
    }

    public Object getParam(String key) {
        return servletRequest.getParameter(key);
    }

    protected Authentication authentication;

    protected UserAuth userAuth;

    public UserAuth getUserAuth() {
        return userAuth;
    }

    public boolean isLogined() {
        return userAuth != null;
    }

    public HttpAction(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
        this.time = System.currentTimeMillis();
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = this.authentication.getPrincipal();
        if (UserAuth.class.isInstance(principal)) {
            this.userAuth = (UserAuth) this.authentication.getPrincipal();
        } else {
            //no auth
            this.userAuth = null;
        }
    }

    public long recordTime() {
        long current = System.currentTimeMillis();
        long passed = current - time;
        time = current;
        return passed;
    }

    public String getRemoteClientIP() {
        String ip = servletRequest.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) {
            ip = servletRequest.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty()) {
            ip = servletRequest.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.isEmpty()) {
            ip = servletRequest.getRemoteAddr();
        }
        return ip;
    }
}
