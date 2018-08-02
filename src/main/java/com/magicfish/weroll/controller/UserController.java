package com.magicfish.weroll.controller;

import com.magicfish.weroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @PostMapping("/login")
    public Object login(//
                        @RequestParam String username, //
                        @RequestParam String password) {
        try {
            return userService.login(username, password);
        } catch (Exception e) {
            return e;
        }
    }
    /*
    @ResponseBody
    @GetMapping("/all")
    public Object findAllUser(
            @RequestParam(name = "pageNum", required = false, defaultValue = "1")
                int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10")
                int pageSize){

        return userService.findAllUser(pageNum, pageSize);
    }
     */
}
