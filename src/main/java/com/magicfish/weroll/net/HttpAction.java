package com.magicfish.weroll.net;

import com.magicfish.weroll.security.jwt.identifier.UserPayload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HttpAction {

    public static HttpAction initialize(HttpAction action, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        if (action == null) {
            action = new HttpAction();
        }
        action.servletRequest = servletRequest;
        action.servletResponse = servletResponse;
        action.init();
        return action;
    }

    public static HttpAction create(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        return new HttpAction(servletRequest, servletResponse);
    }

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

    protected UserPayload userPayload;

    public UserPayload getUserPayload() {
        return userPayload;
    }

    public boolean isLogined() {
        return userPayload != null;
    }

    public HttpAction() {

    }

    public HttpAction(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
        init();
    }

    private void init() {
        this.time = System.currentTimeMillis();
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        if (this.authentication != null) {
            Object principal = this.authentication.getPrincipal();
            if (UserPayload.class.isInstance(principal)) {
                this.userPayload = (UserPayload) this.authentication.getPrincipal();
            } else {
                //no auth
                this.userPayload = null;
            }
        } else {
            //no auth
            this.userPayload = null;
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
