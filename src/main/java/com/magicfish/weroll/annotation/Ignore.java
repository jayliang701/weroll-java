package com.magicfish.weroll.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Ignore {

}
