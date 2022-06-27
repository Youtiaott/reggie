package com.example.reggie.interceptor;

import com.example.reggie.common.BaseContext;
import com.example.reggie.entity.Employee;
import com.example.reggie.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: 页面拦截器
 * date: 2022/6/10 10:31
 * version: 1.0
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        Employee employee = (Employee)request.getSession().getAttribute("employee");

        //如果是移动端
        if(null==employee){
           User user = (User)request.getSession().getAttribute("user");
            //如果符合条件
            if(null!=user){
                log.info("本次请求{}不需要处理",requestURI);

                long id = Thread.currentThread().getId();
                log.info("当前线程：{}",id);

                //获取当前登录用户id保存到该线程中
                BaseContext.setCurrentId(user.getId());

                return true;
            }
            log.info("本次请求{}拦截",requestURI);
            response.sendRedirect(request.getContextPath()+"/front/page/login.html");
            return false;
        }

        //如果是管理端
        if(null!=employee){
            log.info("本次请求{}不需要处理",requestURI);

            long id = Thread.currentThread().getId();
            log.info("当前线程：{}",id);

            //获取当前登录用户id保存到该线程中
            BaseContext.setCurrentId(employee.getId());
            return true;
        }

        log.info("本次请求{}拦截",requestURI);
        response.sendRedirect(request.getContextPath()+"/backend/page/login/login.html");
        return false;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
