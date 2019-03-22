package com.magicfish.weroll.controller;

import com.magicfish.weroll.config.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class AbstractController {

    protected GlobalSetting globalSetting;

    protected ApplicationContext applicationContext;

    protected Logger logger;

    public AbstractController(ApplicationContext applicationContext) throws Exception {
        logger = LoggerFactory.getLogger(this.getClass());
        this.applicationContext = applicationContext;
        globalSetting = GlobalSetting.getInstance();
        injectAnnotation();
        initMiddleWare();
    }

    protected void injectAnnotation() throws Exception {

    }

    protected void initMiddleWare() {

    }
}
