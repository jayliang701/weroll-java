package com.magicfish.weroll.net;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

public interface IHttpInterceptor extends HandlerInterceptor {
    void register(InterceptorRegistry registry);
}
