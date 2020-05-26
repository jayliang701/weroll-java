package com.magicfish.weroll;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RUNTIME)
@Target(TYPE)
@Import(WerollAnnotation.class)
public @interface Weroll {
    String[] apiScan() default "";
    String apiController() default "";
}