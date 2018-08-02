package com.magicfish.weroll.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,  ElementType.PARAMETER})
public @interface Param {
    String name();

    String type();

    boolean required() default true;

    String defaultValue() default "";
}
