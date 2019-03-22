package com.magicfish.weroll.net;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.exception.ServiceException;
import com.netflix.discovery.EurekaClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class ServiceInvoker {

    private LoadBalancerClient loadBalancerClient;

    private EurekaClient discoveryClient;

    private RestTemplate restTemplate;

    private String defaultNode = "";

    public String getDefaultNode() {
        return defaultNode;
    }

    public void setDefaultNode(String defaultNode) {
        this.defaultNode = defaultNode;
    }

    public ServiceInvoker(EurekaClient discoveryClient, LoadBalancerClient loadBalancerClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.loadBalancerClient = loadBalancerClient;
        this.restTemplate = restTemplate;
    }

    public ServiceInvoker use(String node) {
        ServiceInvoker instance = new ServiceInvoker(discoveryClient, loadBalancerClient, restTemplate);
        instance.defaultNode = node;
        return instance;
    }

    public Object call(String method, Object params) throws ServiceException {
        String node = this.defaultNode;
        return this.call(node, method, params);
    }

    public <T> T call(String method, Object params, Class<T> mappingClass) throws ServiceException {
        String node = this.defaultNode;
        return this.call(node, method, params, mappingClass);
    }

    public Object call(String node, String method, Object params) throws ServiceException {
        APIResponseBody result = this.invoke(node, method, params);
        return result.getData();
    }

    public <T> T call(String node, String method, Object params, Class<T> mappingClass) throws ServiceException {
        APIResponseBody result = this.invoke(node, method, params);
        return JSON.toJavaObject((JSON) result.getData(), mappingClass);
    }

    private APIResponseBody invoke(String node, String method, Object params) throws ServiceException {

        ServiceInstance serviceApp = loadBalancerClient.choose(node);
        if (serviceApp == null) {
            throw new ServiceException("can not resolve node [" + node + "]");
        }
        String url = "http://" + serviceApp.getHost() + ":" + serviceApp.getPort() + "/api";

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        JSONObject postBody = new JSONObject();
        postBody.put("method", method);
        postBody.put("data", JSON.toJSON(params));

        HttpEntity<String> entity = new HttpEntity<>(postBody.toJSONString(), headers);
        JSONObject result = restTemplate.postForObject(url, entity, JSONObject.class);
        return JSONObject.toJavaObject(result, APIResponseBody.class);
    }
}
