package com.magicfish.weroll.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@ConfigurationProperties("setting")
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class GlobalConfiguration {

    private static GlobalConfiguration instance;

    public static GlobalConfiguration getInstance() {
        return instance;
    }

    public GlobalConfiguration() {
        instance = this;
    }

    public static void setInstance(GlobalConfiguration instance) {
        GlobalConfiguration.instance = instance;
    }

    private String env;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
        System.out.println("env: " + env);
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