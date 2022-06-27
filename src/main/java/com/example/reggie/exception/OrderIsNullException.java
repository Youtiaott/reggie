package com.example.reggie.exception;

import com.example.reggie.common.R;

/**
 * description: 空订单异常
 * date: 2022/6/26 9:36
 * version: 1.0
 */
public class OrderIsNullException extends RuntimeException{
    public OrderIsNullException(String msg){
        super(msg);
    }
}
