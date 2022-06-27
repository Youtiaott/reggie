package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.entity.User;
import com.example.reggie.mapper.ShoppingCartMapper;
import com.example.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    //添加购物车
    @Override
    @Transactional//开启事务
    public ShoppingCart add(ShoppingCart shoppingCart, HttpSession session) {

        //获取用户id并设置
        User user = (User)session.getAttribute("user");
        shoppingCart.setUserId(user.getId());

        //判断当前是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        if(null!=dishId){
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getUserId,user.getId())
                        .and((qw)->{
                            qw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
                        });
        }else {
            //添加的是套餐
            queryWrapper.eq(ShoppingCart::getUserId,user.getId())
                    .and((qw)->{
                        qw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
                    });
        }

        ShoppingCart result = this.getOne(queryWrapper);

        if(null!=result){
            //如果已经存在，自加1
            Integer number = result.getNumber();
            result.setNumber(number+1);
            this.updateById(result);
        }else{
            //如果不存在就直接保存
            this.save(shoppingCart);
            result=shoppingCart;
        }
        return result;
    }

    //减少购物车对应食品数量
    @Override
    public ShoppingCart subtractNumber(ShoppingCart shoppingCart,Long userId) {

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if(null!=shoppingCart.getDishId()){
            //菜品
            Long dishId = shoppingCart.getDishId();
            //获取购物车中对应菜品的数量
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart result = this.getOne(queryWrapper);
            Integer number = result.getNumber();

            if(number>1){
                //将对应菜品数量减少
                result.setNumber(number-1);
                //更新对应菜品数量
                this.updateById(result);
                shoppingCart=result;
            }else{
                this.removeById(result.getId());
            }


        }else {
            //套餐
            Long setmealId = shoppingCart.getSetmealId();

            //获取购物车中对应菜品的数量
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            ShoppingCart result = this.getOne(queryWrapper);
            Integer number = result.getNumber();

            if(number>1){
                //将对应套餐数量减少
                result.setNumber(number-1);
                this.updateById(result);
                shoppingCart=result;
            }else {
                this.removeById(result.getId());
            }

        }
        return shoppingCart;
    }

    @Override
    public List<ShoppingCart> listAllById(Long userId) {
        if(null==userId)
            return null;

        List<ShoppingCart> shoppingCartList = this.list(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, userId));
        return shoppingCartList;

    }
}
