package com.example.reggie.exception;

/**
 * description: 自定义业务异常
 * date: 2022/6/14 15:09
 * version: 1.0
 */
public class CustomException extends RuntimeException{
    public CustomException(String msg){
        super(msg);
    }
}
