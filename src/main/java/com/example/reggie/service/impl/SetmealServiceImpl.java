package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    //保存|批量保存
    @Override
    @Transactional//开启事务
    public void saveSetmeal(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);

        //获取套餐中菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //设置每个菜品的套餐id
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

       //批量保存
        setmealDishService.saveBatch(setmealDishes);
    }

    //删除|批量删除
    @Override
    @Transactional//开启事务
    public void delete(Long[] ids) {
        //将数组转为list
        ArrayList<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);

        //先将指定套餐菜品表的数据删除
        LambdaUpdateWrapper<SetmealDish> setmealDishUpdateWrapper = new LambdaUpdateWrapper<>();
        setmealDishUpdateWrapper.in(SetmealDish::getSetmealId,idList);
        setmealDishService.remove(setmealDishUpdateWrapper);

        //再将指定套餐表中数据删除
        this.removeByIds(idList);
    }

    /***
     * @Description //TODO 根据id返回setmeal
     * @param id
     * @return: com.example.reggie.dto.SetmealDto
     **/
    @Override
    public SetmealDto findSetmealByID(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        //查找套餐基本信息
        Setmeal setmeal = this.getById(id);
        //将套餐基本信息拷贝到setmealDto
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查找套餐菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        //将套餐下所有菜品拷贝到setmealDto中的SetmealDishes属性
        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;
    }

    /***
     * @Description //TODO 修改套餐
     * @param setmealDto
     * @return: void
     **/
    @Override
    @Transactional
    public void updataSetmeal(SetmealDto setmealDto) {
        //更新修改后的套餐信息
        this.updateById(setmealDto);

        //获取修改后的套餐中的菜品数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //删除原来的套餐菜品表中的数据
        LambdaUpdateWrapper<SetmealDish> setmealDishUpdateWrapper = new LambdaUpdateWrapper<>();
        setmealDishUpdateWrapper.in(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(setmealDishUpdateWrapper);

        //通过stream的方式将每个菜品设置上套餐的id
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //重新添加
        setmealDishService.saveBatch(setmealDishes);
    }
}
