package com.magicfish.weroll.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.magicfish.weroll.annotation.Router;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@ConfigurationProperties("setting")
@PropertySource("classpath:setting.properties")
public class GlobalConfiguration {

    private static GlobalConfiguration instance;

    public static GlobalConfiguration getInstance() {
        return instance;
    }

    public GlobalConfiguration() {
        instance = this;
    }

    private APIConfiguration api;

    public APIConfiguration getApi() {
        return api;
    }

    public void setApi(APIConfiguration api) {
        this.api = api;
    }

    private RouterConfiguration router;

    public RouterConfiguration getRouter() {
        return router;
    }

    public void setRouter(RouterConfiguration router) {
        this.router = router;
    }

    private AuthConfiguration auth;

    public AuthConfiguration getAuth() {
        return auth;
    }

    public void setAuth(AuthConfiguration auth) {
        this.auth = auth;
    }
}