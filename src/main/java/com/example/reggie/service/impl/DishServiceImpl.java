package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
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
 * 菜品管理 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /***
     * @Description //TODO 保存新增的菜品
     * @return: void
     **/
    @Transactional//开启事务
    public void add(DishDto dishDto){
        //保存菜品的基本信息到菜品表中
        this.save(dishDto);

        //获取菜品id(id由雪花算法在数据存入数据库时自动生成)
        Long id = dishDto.getId();

        //获取菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //通过stream的方式将每种口味设置上菜品的id
        flavors.stream().map((item)->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //将口味批量保存到菜品的口味表
        dishFlavorService.saveBatch(flavors);
    }

    /*** 
     * @Description //TODO 删除菜品操作
     * @param ids 
     * @return: void
     **/
    @Override
    @Transactional//开启事务提交
    public void deleteByIds(Long[] ids) {
        //将数组转为list集合
        ArrayList<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);

        //先把菜品口味表中的数据删除
        LambdaUpdateWrapper<DishFlavor> dishFlavorUpdateWrapper = new LambdaUpdateWrapper<>();
        dishFlavorUpdateWrapper.in(DishFlavor::getDishId,idList);
        dishFlavorService.remove(dishFlavorUpdateWrapper);

        //再删除菜品表中的数据
        LambdaUpdateWrapper<Dish> dishUpdateWrapper = new LambdaUpdateWrapper<>();
        dishUpdateWrapper.in(Dish::getId,idList);
        this.remove(dishUpdateWrapper);

    }

    /*** 
     * @Description //TODO 用于修改菜品数据回显
     * @param id 
     * @return: com.example.reggie.dto.DishDto
     **/
    @Override
    public DishDto findById(long id) {
        //查询菜品基本信息
        LambdaQueryWrapper<Dish> DishQueryWrapper = new LambdaQueryWrapper<>();
        DishQueryWrapper.eq(Dish::getId,id);
        Dish dish = this.getOne(DishQueryWrapper);
        //查询菜品口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorQueryWrapper);

        //将菜品所有信息保存到disDto
        DishDto dishDto = new DishDto();
        //将dish的内容拷贝到dishDto内（dish,dishDto为继承关系）
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(dishFlavorList);

        return dishDto;
    }

    /*** 
     * @Description //TODO 更新菜品信息，同时更新口味信息
     * @param dishDto 
     * @return: void
     **/
    @Override
    public void updataDish(DishDto dishDto) {
        //更新菜品表信息
        this.updateById(dishDto);
        //清除对应菜品口味表信息
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishFlavorQueryWrapper);


        //获取更新后的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        //通过stream的方式将每种口味设置上菜品的id
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //重新添加进菜品口味表
        dishFlavorService.saveBatch(flavors);
    }

    //查找菜品口味
    @Override
    public List<DishFlavor> findDishFlavor(long id) {
        List<DishFlavor> dishFlavorList = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
        return dishFlavorList;
    }
}
