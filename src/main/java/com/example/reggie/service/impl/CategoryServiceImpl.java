package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.exception.CustomException;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜品及套餐分类 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void DeleteById(long ids) {
        //查询当前分类是否有关联菜品，如果有关联则抛出一个异常
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(dishQueryWrapper);

        //有关联，不允许删除，抛出业务异常
        if(count>0){
            throw new CustomException("当前分类关联了菜品，不能删除！");
        }


        //查询当前分类是否有关联套餐，如果有关联则抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,ids);
        count = setmealService.count(setmealQueryWrapper);

        //有关联，不允许删除，抛出业务异常
        if(count>0){
            throw new CustomException("当前分类关联了套餐，不能删除！");
        }

        //没有关联，允许删除
        removeById(ids);

    }
}
