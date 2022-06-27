package com.example.reggie.controller;


import com.example.reggie.common.R;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.entity.User;
import com.example.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /***
     * @Description //TODO 添加购物车
     * @param shoppingCart
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        log.info("shoppingCart:{}",shoppingCart.toString());
        if(null == shoppingCart){
            return R.error("空数据，null");
        }

        ShoppingCart result = shoppingCartService.add(shoppingCart, session);

        return R.success(result);
    }

    /***
     * @Description //TODO 减少购物车食品数量
     * @param shoppingCart
     * @return: com.example.reggie.common.R<com.example.reggie.entity.ShoppingCart>
     **/
    @PostMapping("/sub")
    public R<ShoppingCart> subtractShoppingCart(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        if(null == shoppingCart){
            return R.error("空数据，null");
        }

        User user =(User)session.getAttribute("user");
        ShoppingCart result = shoppingCartService.subtractNumber(shoppingCart, user.getId());
        return R.success(result);
    }

    /***
     * @Description //TODO 获取当前购物车内容
     * @param session 
     * @return: com.example.reggie.common.R<java.util.List<com.example.reggie.entity.ShoppingCart>>
     **/
    @GetMapping("/list")
    public R<List<ShoppingCart>> lookShoppingCart(HttpSession session){
        User user = (User)session.getAttribute("user");
        List<ShoppingCart> shoppingCarts = shoppingCartService.listAllById(user.getId());
        if(null==shoppingCarts)
            return R.error("当前用户没有选择商品！");

        return R.success(shoppingCarts);
    }
}

