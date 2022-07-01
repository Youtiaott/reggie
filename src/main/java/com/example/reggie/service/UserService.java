package com.example.reggie.service;

import com.example.reggie.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
public interface UserService extends IService<User> {
    String sendMsg(String phone);
    User loginValida(Map<String,String> map);

}
