package com.magicfish.weroll.controller;

import com.magicfish.weroll.model.APIPostBody;
import com.magicfish.weroll.net.APIRequest;
import com.magicfish.weroll.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class APIController {

    @Autowired
    private APIService service;

    @ResponseBody
    @PostMapping("/api")
    public Object api(@RequestBody APIPostBody body, HttpServletRequest servletRequest) {
        APIRequest request = new APIRequest(servletRequest, body);
        return service.exec(request);
    }
}
