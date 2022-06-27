package com.example.reggie.exception;

/**
 * description: 验证码验证异常
 * date: 2022/6/23 11:21
 * version: 1.0
 */
public class CodeValidaException extends RuntimeException{
    public CodeValidaException(String msg){
        super(msg);
    }
}
