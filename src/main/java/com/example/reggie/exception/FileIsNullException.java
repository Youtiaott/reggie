package com.example.reggie.exception;

import java.io.FileNotFoundException;

/**
 * description: 菜品图片找不到
 * date: 2022/6/16 9:14
 * version: 1.0
 */
public class FileIsNullException extends RuntimeException {
    public FileIsNullException(String msg){
        super(msg);
    }
}
