package com.magicfish.weroll.router;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class User {

    @GetMapping("/login")
    public Object renderLoginPage() {
        return "/login";
    }

    @GetMapping("/register")
    public Object renderRegisterPage() {
        return "/register";
    }
}
