package com.example.reggie.exception;

import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;


/**
 * description: 全局异常处理器
 * date: 2022/6/11 16:40
 * version: 1.0
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//处理 类上加了这些注解的controller
@ResponseBody
@Slf4j
public class GlobalException {

    /***
     * @Description //TODO 处理SQLIntegrityConstraintViolationException异常的方法
     * @param ex
     * @return: com.example.reggie.result.R<java.lang.String>
     **/
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> addEmployeeException(SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            //拼接字符串，截取账号名
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"已存在";
            return R.error("账号："+msg);
        }
        return R.error("添加失败");
    }

    /*** 
     * @Description //TODO 处理关联自定义异常
     * @param ex 
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        return R.error(ex.getMessage());
    }

    /*** 
     * @Description //TODO 处理登录验证码错误
     * @param ex 
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @ExceptionHandler(CodeValidaException.class)
    public R<String> codeValida(CodeValidaException ex){
        return R.error(ex.getMessage());
    }

    /***
     * @Description //TODO  处理 空订单异常
     * @param ex
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @ExceptionHandler(OrderIsNullException.class)
    public R<String> orderIsNullException(OrderIsNullException ex){
        return R.error(ex.getMessage());
    }
}
