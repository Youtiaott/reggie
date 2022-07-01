package com.example.reggie.service;

import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.DishFlavor;

import java.util.List;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
public interface DishService extends IService<Dish> {

    public void add(DishDto dishDto);
    public void deleteByIds(Long[] ids);
    public DishDto findById(long dishId);
    public void updataDish(DishDto dishDto);
    List<DishFlavor> findDishFlavor(long dishId);
    List<DishDto> findAllByCategoryId(Long categoryId, Integer status);

}
