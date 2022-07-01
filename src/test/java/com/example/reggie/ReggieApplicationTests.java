package com.example.reggie;

import com.example.reggie.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.function.Function;

@SpringBootTest
class ReggieApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        List<String> mylist = redisTemplate.opsForList().range("mylist", 0, -1);
        for (String s : mylist) {
            System.out.println(s);
        }
    }

    @Test
    void test(){
        redisTemplate.opsForList().leftPushAll("mylist","a","b","c");
    }
}
