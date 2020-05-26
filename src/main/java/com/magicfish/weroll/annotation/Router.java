package com.magicfish.weroll.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Inherited
public @interface Router {

    String path();

    String view() default "";

    boolean needLogin() default false;

}
