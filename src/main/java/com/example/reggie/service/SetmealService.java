package com.example.reggie.service;

import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
public interface SetmealService extends IService<Setmeal> {
    void saveSetmeal(SetmealDto setmealDto);

    void delete(Long[] ids);

    SetmealDto findSetmealByID(Long id);

    void updataSetmeal(SetmealDto setmealDto);
}
