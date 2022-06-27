package com.example.reggie.service;

import com.example.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.User;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
public interface OrdersService extends IService<Orders> {
    void submit(Orders order, User user);

}
