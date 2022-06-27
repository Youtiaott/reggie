package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Orders;
import com.example.reggie.entity.User;
import com.example.reggie.service.OrdersService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /*** 
     * @Description //TODO 用户下单
     * @param order 
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order, HttpSession session){
        User user = (User) session.getAttribute("user");
        ordersService.submit(order,user);
        return R.success("下单成功俺也不会配送的");
    }

    /***
     * @Description //TODO 管理端分页查看订单
     * @param page
     * @param pageSize
     * @param orderId
     * @return: com.example.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.example.reggie.entity.Orders>>
     **/
    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, Integer orderId){
        Page<Orders> pageInfo = new Page<Orders>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        //过滤条件
        queryWrapper.like(null!=orderId,Orders::getNumber,orderId);
        //排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);

    }

    /***
     * @Description //TODO 用户端分页查看订单
     * @param page
     * @param pageSize
     * @param orderId
     * @return: com.example.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.example.reggie.entity.Orders>>
     **/
    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(int page, int pageSize, Long orderId){
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        //过滤条件
        queryWrapper.like(orderId!=null,Orders::getNumber,orderId);
        //排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);

    }

}

