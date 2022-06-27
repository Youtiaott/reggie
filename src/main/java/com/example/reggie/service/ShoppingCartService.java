package com.example.reggie.service;

import com.example.reggie.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    ShoppingCart add(ShoppingCart shoppingCart, HttpSession session);
    ShoppingCart subtractNumber(ShoppingCart shoppingCart,Long userId);
    List<ShoppingCart> listAllById(Long userId);
}
