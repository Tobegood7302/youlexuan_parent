package com.cc.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cc.service.IUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Reference
    private IUserService userService;

    @RequestMapping("/getName")
    public String getName(){
        return userService.getName();
    }

}
