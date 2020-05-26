package com.magicfish.weroll.controller;

import com.magicfish.weroll.config.GlobalSetting;
import com.magicfish.weroll.net.APIAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractHttpProcessor implements IHttpProcessor {

    protected GlobalSetting globalSetting;

    protected ApplicationContext applicationContext;

    protected Logger logger;

    public AbstractHttpProcessor(ApplicationContext applicationContext) throws Exception {
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

    public Object process(APIAction action) throws Exception {
        return null;
    }

    public Object process(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null;
    }
}
