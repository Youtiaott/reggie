package com.example.reggie.controller;


import com.example.reggie.common.R;
import com.example.reggie.entity.User;
import com.example.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    /***
     * @Description //TODO 发送短信验证码
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        //获取手机号
        String phone = user.getPhone();
        //如果不为空
        if(StringUtils.isNotEmpty(phone)){
            String code = userService.sendMsg(phone);
            //session.setAttribute(phone,code);

            return R.success("发送验证码成功");
        }
        return R.error("验证码发送失败,手机号可能为空或出现异常");
    }

    /*** 
     * @Description //TODO 用户登录
     * @param map 
     * @param session
     * @return: com.example.reggie.common.R<com.example.reggie.entity.User>
     **/
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,String> map,HttpSession session){

        log.info(map.toString());
        if(map.isEmpty()){
            return R.error("出现错误,空数据");
        }
        User user = userService.loginValida(map);
        session.setAttribute("user",user);
        return R.success(user);
    }
}

