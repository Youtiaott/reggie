package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /*** 
     * @Description //TODO 菜品分页展示
     * @param page 
     * @param pageSize
     * @param name
     * @return: com.example.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     **/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        //用来查询dish分页数据
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        //用于返回菜品及分类的数据
        Page<DishDto> pageDto = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //将pageInfo除了records之外的属性拷贝到pageDto
        //将分页数据（total，size。。）拷贝到pageDto
        BeanUtils.copyProperties(pageInfo,pageDto,"records");

        //从分页查询获取菜品基本信息
        List<Dish> records = pageInfo.getRecords();

        //item是每次遍历的Dish对象
        List<DishDto> list = records.stream().map((item)->{
            //将Dish菜品的基本信息拷贝到dishDto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //拿到分类id查询分类名字
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(null!=category){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        //将pageInfo中处理好的的Records替换到pageDto中
        pageDto.setRecords(list);

        return R.success(pageDto);
    }

    /*** 
     * @Description //TODO 新增菜品
     * @param dishDto 
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){

        dishService.add(dishDto);
        return R.success("新增菜品成功");
    }

    /***
     * @Description //TODO 删除菜品
     * @param ids
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @DeleteMapping
    public R<String> deleteDish(Long[] ids){
        dishService.deleteByIds(ids);
        return R.success("删除成功！");
    }

    /*** 
     * @Description //TODO 用于回显编辑菜品信息
     * @param id 
     * @return: com.example.reggie.common.R<com.example.reggie.entity.Dish>
     **/
    @GetMapping("/{id}")
    public R<DishDto> findFish(@PathVariable("id") Long id){

        DishDto dishDto = dishService.findById(id);
        return R.success(dishDto);
    }

    /***
     * @Description //TODO 修改菜品
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PutMapping
    public R<String> Edit(@RequestBody DishDto dishDto){

        dishService.updataDish(dishDto);

        return R.success("修改成功！");
    }

    /***
     * @Description //TODO 查询不同分类下的所有菜品
     * @param categoryId
     * @return: com.example.reggie.common.R<java.util.List<com.example.reggie.entity.Dish>>
     **/
    @GetMapping("/list")
    public R<List<DishDto>> list(long categoryId,Integer status){

        List<DishDto> dishDtos = dishService.findAllByCategoryId(categoryId, status);
        return R.success(dishDtos);
    }

    /***
     * @Description //TODO 停售
     * @param ids
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/status/0")
    public R<String> updataStatus0(Long[] ids){

        //将ids数组转换为list集合
        List<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId,idList).set(Dish::getStatus,0);
        dishService.update(null,updateWrapper);

        return R.success("修改成功！");
    }

    /***
     * @Description //TODO 起售
     * @param ids
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/status/1")
    public R<String> updataStatus1(Long[] ids){
        //将ids数组转换为list集合
        List<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId,idList).set(Dish::getStatus,1);
        dishService.update(null,updateWrapper);

        return R.success("修改成功！");
    }
}

