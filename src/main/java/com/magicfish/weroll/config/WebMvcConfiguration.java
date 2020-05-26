package com.magicfish.weroll.config;

import com.magicfish.weroll.net.IHttpInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
@EnableWebSecurity
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private GlobalSetting globalSetting;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        String[] resourceHandlers = globalSetting.getRes().getHandlers();
        ResourceHandlerRegistration registration = null;
        for (String item : resourceHandlers) {
            registration = registry.addResourceHandler(item);
        }
        if (registration != null) {
            String[] resourceLocations = globalSetting.getRes().getLocations();
            for (String item : resourceLocations) {
                registration.addResourceLocations(item);
            }
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (globalSetting.getApi().getEnableCors()) {
            registry.addInterceptor(new CORSInterceptor(globalSetting)).addPathPatterns("/**");
        }
        String[] interceptors = globalSetting.getApi().getInterceptors();
        for (int i = 0; i < interceptors.length; i++) {
            try {
                Class<?> cls = Class.forName(interceptors[i]);
                IHttpInterceptor interceptor = (IHttpInterceptor) cls.newInstance();
                interceptor.register(registry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class CORSInterceptor implements HandlerInterceptor {

    private GlobalSetting globalSetting;

    public CORSInterceptor(GlobalSetting globalSetting) {
        this.globalSetting = globalSetting;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

//        String origin = httpServletRequest.getHeader("Origin");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", globalSetting.getApi().getCorsAllowOriginals());
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Accept,Authorization,Cache-Control,Content-Type,DNT,If-Modified-Since,Keep-Alive,Origin,User-Agent,X-Requested-With");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        return true;
    }
}
