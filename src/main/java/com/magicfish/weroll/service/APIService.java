package com.magicfish.weroll.service;

import com.magicfish.weroll.net.APIRequest;
import org.springframework.stereotype.Component;

@Component
public class APIService {
    public Object exec(APIRequest request) {
        try {
            System.out.println("exec api...");
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return request.getPostBody();
    }
}
