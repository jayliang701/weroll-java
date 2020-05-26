package com.magicfish.weroll.annotation;

import com.magicfish.weroll.net.ResponseBodyProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RestGet {
    String[] value() default "";

    boolean needLogin() default false;

    Class<?> processor() default ResponseBodyProcessor.class;
}
