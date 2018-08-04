package com.magicfish.weroll.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,  ElementType.PARAMETER})
public @interface Method {
    String name();

    Param[] params() default {};

    boolean needLogin() default false;
}
