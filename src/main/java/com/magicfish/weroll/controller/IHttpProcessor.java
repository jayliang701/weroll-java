package com.magicfish.weroll.controller;

import com.magicfish.weroll.net.APIAction;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IHttpProcessor {
    Object process(APIAction action) throws Exception;

    Object process(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
