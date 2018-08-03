package com.magicfish.weroll.net;


import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.consts.ErrorCodes;
import com.magicfish.weroll.exception.ServiceException;
import com.magicfish.weroll.model.APIPostBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public class APIAction extends HttpAction {

    private APIPostBody postBody;

    public APIPostBody getPostBody() {
        return postBody;
    }

    public String getMethod() {
        return postBody.getMethod();
    }

    @Override
    public HashMap<String, ?> getParams() {
        return postBody.getData();
    }

    @Override
    public Object getParam(String key) {
        return this.getParams().get(key);
    }

    public APIAction(HttpServletRequest servletRequest, HttpServletResponse servletResponse, APIPostBody postBody) {
        super(servletRequest, servletResponse);
        this.postBody = postBody;
    }

    public Object sayOK(Object data) {
        JSONObject result = new JSONObject();
        result.put("code", ErrorCodes.OK);
        result.put("data", data);
        return result;
    }

    public Object sayError() {
        JSONObject result = new JSONObject();
        result.put("code", ErrorCodes.SERVER_ERROR);
        result.put("msg", "unknown");
        return result;
    }

    public Object sayError(String msg) {
        JSONObject result = new JSONObject();
        result.put("code", ErrorCodes.SERVER_ERROR);
        result.put("msg", msg);
        return result;
    }

    public Object sayError(int code, String msg) {
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    public Object sayError(ServiceException e) {
        JSONObject result = new JSONObject();
        result.put("code", e.getCode());
        result.put("msg", e.getMessage());
        return result;
    }
}
