package com.magicfish.weroll.controller;

import com.magicfish.weroll.config.GlobalConfiguration;
import com.magicfish.weroll.utils.ClassUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AbstractController {

    protected GlobalConfiguration globalConfiguration;

    public AbstractController() throws Exception {
        globalConfiguration = GlobalConfiguration.getInstance();
        injectAnnotation();
        initMiddleWare();
    }

    protected String[] getInjectionPackages() {
        return new String[] {};
    }

    protected void injectAnnotation() throws Exception {
        String[] packages = getInjectionPackages();
        for (String path : packages) {
            findAllMethodAnnotation(ClassUtil.getClasses(path));
        }
    }

    protected void initMiddleWare() {

    }

    protected void findAllMethodAnnotation(Set<Class<?>> clsSet) throws Exception {
        findAllMethodAnnotation(new ArrayList<>(clsSet));
    }

    protected void findAllMethodAnnotation(List<Class> clsList) throws Exception {

    }
}
