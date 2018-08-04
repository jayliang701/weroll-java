package com.magicfish.weroll.config;

public class RouterConfiguration {

    private String basePackage;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    private String pageNotFound;

    public String getPageNotFound() {
        return pageNotFound;
    }

    public void setPageNotFound(String pageNotFound) {
        this.pageNotFound = pageNotFound;
    }
}
