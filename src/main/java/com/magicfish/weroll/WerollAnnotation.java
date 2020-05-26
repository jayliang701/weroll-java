package com.magicfish.weroll;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ComponentScan(
    basePackages = {
        "com.magicfish.weroll.utils",
        "com.magicfish.weroll.config",
        "com.magicfish.weroll.security",
        "com.magicfish.weroll.controller",
        "com.magicfish.weroll.net"
    },
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {  })}
)
public class WerollAnnotation {
}
