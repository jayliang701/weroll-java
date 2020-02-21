package com.magicfish.weroll.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ViewTemplateConfiguration {

    @Autowired
    private GlobalSetting globalSetting;

    public GlobalSetting getGlobalSetting() {
        return globalSetting;
    }

    public String getResDomain() {
        return globalSetting.getRes().getCdn();
    }

    public String getSiteDomain() {
        return globalSetting.getSite().getDomain();
    }

    public String getSiteTitle() {
        return globalSetting.getSite().getTitle();
    }

    public String toResUrl(String path) {
        String url = getResDomain();
        if (url.endsWith("/") && path.startsWith("/")) {
            return url + path.substring(1);
        }
        return url + path;
    }

    public String toSiteUrl(String path) {
        String url = getSiteDomain();
        if (url.endsWith("/") && path.startsWith("/")) {
            return url + path.substring(1);
        }
        return url + path;
    }

}
