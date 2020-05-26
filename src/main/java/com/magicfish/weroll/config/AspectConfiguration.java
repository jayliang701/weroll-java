package com.magicfish.weroll.config;

import com.magicfish.weroll.aspect.RestfulAspect;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.LOWEST_PRECEDENCE)
@Configuration
@EnableAspectJAutoProxy
public class AspectConfiguration {
    @Bean
    public RestfulAspect restfulAspect(ApplicationContext applicationContext) throws Exception{
        return new RestfulAspect(applicationContext);
    }
}