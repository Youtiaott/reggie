package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.entity.User;
import com.example.reggie.exception.CodeValidaException;
import com.example.reggie.mapper.UserMapper;
import com.example.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.utils.SMSUtils;
import com.example.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //发送短信
    @Override
    public String sendMsg(String phone) {
        //随机验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("验证码:{}",code);
        //发送短信
        //SMSUtils.sendMessage(phone,code);

        //将验证码存入redis并设置有效期
        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
        return code;
    }

    //登陆验证
    @Override
    @Transactional//开启事务
    public User loginValida(Map<String, String> map) {
        String phone = map.get("phone");
        String code = map.get("code");
        
        //判断验证码
        String code_redis = redisTemplate.opsForValue().get(phone);
        if(!code.equals(code_redis)){
            throw new CodeValidaException("验证码错误或验证码过期！");
        }
        //判断完成删除redis缓存的code
        redisTemplate.delete(phone);

        //根据手机号查找用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User result = getOne(queryWrapper);

        //如果是新用户就注册
        if(null==result){
            User user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            this.save(user);
            return user;
        }
        return result;
    }

}
