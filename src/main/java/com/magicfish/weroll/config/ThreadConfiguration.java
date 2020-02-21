package com.magicfish.weroll.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.magicfish.weroll.config.property.ThreadProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// @Configuration
// @ComponentScan("com.magicfish.weroll.controller")
// @EnableAsync
public class ThreadConfiguration {

    Logger logger = LoggerFactory.getLogger(ThreadConfiguration.class);

    @Autowired
    private GlobalSetting globalSetting;

    @Bean(name = "request")
    public Executor asyncExecutor() {
        String namePrefix = "request-Async-";
        ThreadProperties config = globalSetting.getThread();

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(namePrefix);
        threadPoolTaskExecutor.setCorePoolSize(config.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setQueueCapacity(600);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();

        logger.info("setup [request] async executor ---> Core Pool Size: " + config.getCorePoolSize());

        return threadPoolTaskExecutor;
    }

    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}