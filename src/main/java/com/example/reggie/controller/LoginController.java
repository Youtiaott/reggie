package com.example.reggie.controller;

import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * description: 默认登陆页面
 * date: 2022/6/21 7:58
 * version: 1.0
 */
@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(Device device){
        //如果是移动端
        if(device.isMobile()||device.isTablet()){
            return "forward:/front/page/login.html";
        }
        //电脑端
        return "forward:/backend/page/login/login.html";
    }
}
