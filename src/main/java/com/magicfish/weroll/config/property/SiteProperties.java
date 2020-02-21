package com.magicfish.weroll.config.property;

public class SiteProperties extends AbstractProperties {

    private String domain = "";

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    private String title = "Weroll For Java";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
