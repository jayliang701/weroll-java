package com.magicfish.weroll.config;

import com.magicfish.weroll.aspect.RestfulAspect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfiguration {
    @Bean
    public RestfulAspect restfulAspect(){
        return new RestfulAspect();
    }
}