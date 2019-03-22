package com.magicfish.weroll.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.magicfish.weroll.annotation.API;
import com.magicfish.weroll.annotation.Invoker;
import com.magicfish.weroll.annotation.InvokerProvider;
import com.magicfish.weroll.annotation.RouterGroup;
import com.magicfish.weroll.net.ServiceInvoker;
import com.netflix.discovery.EurekaClient;
import org.apache.commons.lang.IllegalClassException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@ConditionalOnProperty("eureka.client.enabled")
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class InvokerConfiguration {

    private ApplicationContext applicationContext;

    private LoadBalancerClient loadBalancerClient;

    public LoadBalancerClient getLoadBalancerClient() {
        return loadBalancerClient;
    }

    @Autowired
    public void setLoadBalancerClient(LoadBalancerClient loadBalancerClient) throws Exception {
        this.loadBalancerClient = loadBalancerClient;
        this.setup();
    }

    private EurekaClient discoveryClient;

    public EurekaClient getDiscoveryClient() {
        return discoveryClient;
    }

    @Autowired
    public void setDiscoveryClient(EurekaClient discoveryClient) throws Exception {
        this.discoveryClient = discoveryClient;
        this.setup();
    }

    private RestTemplate restTemplate;

    public InvokerConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

//    private void autoInject(ApplicationContext applicationContext) throws Exception {
//        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(InvokerProvider.class);
//        for (Map.Entry<String, Object> entry : beans.entrySet()) {
//            Object ins = entry.getValue();
//            Class cls = ins.getClass();
//
//            Field[] fields = cls.getDeclaredFields();
//            for (Field field : fields) {
//                if (field.getName().equals("service")) {
//                    break;
//                }
//            }
//            cls.
//        }
//    }

    private void setup() throws Exception {
        if (discoveryClient == null || loadBalancerClient == null) return;

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(GlobalSetting.getInstance().getApi().getReadTimeout());
        requestFactory.setConnectTimeout(GlobalSetting.getInstance().getApi().getConnectionTimeout());
        restTemplate = new RestTemplate(requestFactory);

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);

        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        fastConverter.setFastJsonConfig(fastJsonConfig);

        restTemplate.setMessageConverters(Arrays.asList(stringHttpMessageConverter, fastConverter));

        Class[] scanMap = {
                API.class, RouterGroup.class, InvokerProvider.class,
        };
        for (Class cls : scanMap) {
            this.register(applicationContext.getBeansWithAnnotation(cls));
        }
    }

    private void register(Map<String, Object> beans) throws Exception {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object ins = entry.getValue();
            Class cls = ins.getClass();

            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                Invoker annotation = field.getAnnotation(Invoker.class);
                if (annotation != null) {
                    String name = annotation.name();

                    if (field.getType().equals(ServiceInvoker.class)) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        ServiceInvoker invoker = new ServiceInvoker(discoveryClient, loadBalancerClient, restTemplate);
                        invoker.setDefaultNode(name);
                        field.set(ins, invoker);
                        field.setAccessible(accessible);
                    } else {
                        throw new IllegalClassException("@Invoker can only be used for ServiceInvoker type");
                    }
                }
            }
        }
    }

}