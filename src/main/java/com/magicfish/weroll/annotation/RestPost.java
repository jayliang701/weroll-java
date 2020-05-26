package com.magicfish.weroll.annotation;

import com.magicfish.weroll.net.JSONRequestBodyFilter;
import com.magicfish.weroll.net.ResponseBodyProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RestPost {
    String[] value() default "";

    boolean needLogin() default false;

    Class<?> filter() default JSONRequestBodyFilter.class;

    Class<?> processor() default ResponseBodyProcessor.class;
}
