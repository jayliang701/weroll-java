package com.magicfish.weroll.net;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpMessageConverterFactory {
    public static AbstractHttpMessageConverter createJSONConverter() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        //从1.1.41升级到1.2.之后的版本必须配置，否则会报错
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        fastConverter.setSupportedMediaTypes(supportedMediaTypes);
        //添加fastjson的配置信息
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteNullStringAsEmpty);
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        //在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }

    public static AbstractHttpMessageConverter createTextConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.TEXT_PLAIN);
        converter.setSupportedMediaTypes(mediaTypeList);
        return converter;
    }
}
