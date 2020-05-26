package com.magicfish.weroll.config.property;

public class APIProperties extends AbstractProperties {

    private Integer readTimeout = 0;

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    private Integer connectionTimeout = 0;

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    private Boolean enableCors = false;

    public Boolean getEnableCors() {
        return enableCors;
    }

    public void setEnableCors(Boolean enableCors) {
        this.enableCors = enableCors;
    }

    private String corsAllowOriginals = "*";

    public String getCorsAllowOriginals() {
        return corsAllowOriginals;
    }

    public void setCorsAllowOriginals(String corsAllowOriginals) {
        this.corsAllowOriginals = corsAllowOriginals;
    }

    private String processor = "com.magicfish.weroll.controller.APIProcessor";

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    private String[] interceptors = new String[]{};

    public String[] getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(String[] interceptors) {
        this.interceptors = interceptors;
    }
}
