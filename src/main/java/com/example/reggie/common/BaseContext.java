package com.example.reggie.common;

/**
 * description: 基于ThreadLocal封装的工具类，用于保存和获取当前用户登录id
 * date: 2022/6/13 15:45
 * version: 1.0
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
