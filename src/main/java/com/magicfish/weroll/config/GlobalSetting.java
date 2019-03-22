package com.magicfish.weroll.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration("globalSetting")
@ConfigurationProperties(prefix="setting")
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class GlobalSetting extends BaseGlobalSetting {

    private static GlobalSetting instance;

    public static GlobalSetting getInstance() {
        return instance;
    }

    public GlobalSetting() {
        instance = this;
    }
}