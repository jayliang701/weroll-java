package com.magicfish.weroll.net;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IResponseBodyProcessor {
    public Object doProcess(Object responseBody, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
