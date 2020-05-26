package com.magicfish.weroll.net;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponseBodyProcessor implements IResponseBodyProcessor {
    @Override
    public Object doProcess(Object responseBody, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return responseBody;
    }
}
