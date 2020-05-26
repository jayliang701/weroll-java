package com.magicfish.weroll.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.magicfish.weroll.controller.APIProcessor;
import com.magicfish.weroll.controller.IHttpProcessor;
import com.magicfish.weroll.controller.RouterProcessor;
import com.magicfish.weroll.net.HttpMessageConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.nio.charset.StandardCharsets;

@Configuration
public class HttpConfiguration {

    Logger logger = LoggerFactory.getLogger(HttpConfiguration.class);

//    @Bean
//    public HttpMessageConverters httpMessageConverters() {
//        HttpMessageConverter<?> json = HttpMessageConverterFactory.createJSONConverter();
//        HttpMessageConverter<?> text = HttpMessageConverterFactory.createTextConverter();
//        return new HttpMessageConverters(json, text);
//    }

    @Bean("api")
    public IHttpProcessor apiController(ApplicationContext applicationContext) throws Exception {
        IHttpProcessor processor = new APIProcessor(applicationContext);
        return processor;
    }

    @Bean("router")
    public IHttpProcessor routerController(ApplicationContext applicationContext) throws Exception {
        IHttpProcessor processor = new RouterProcessor(applicationContext);
        return processor;
    }
}
