package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /***
     * @Description //TODO 用于分页展示
     * @param page
     * @param pageSize
     * @param name
     * @return: com.example.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     **/
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> pageDto = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        setmealQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName,name);
        setmealQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行分类查询
        setmealService.page(pageInfo,setmealQueryWrapper);
        //将分页数据拷贝
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<Setmeal> records = pageInfo.getRecords();
        //处理pageInfo中查询到的套餐信息
        //item为遍历的每一个套餐对象
        List<SetmealDto> list = records.stream().map((item)->{
            //将套餐基本信息拷贝到setmealDto
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //获取分类id并查询出分类表中的名字
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(null!=category){
                String categoryName = category.getName();
                //将查询到的分类名设置
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        //将处理好的Records数据给pageDto
        pageDto.setRecords(list);
        return R.success(pageDto);
    }

    /***
     * @Description //TODO 新增套餐
     * @param setmealDto
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> add(@RequestBody SetmealDto setmealDto){
        setmealService.saveSetmeal(setmealDto);
        return R.success("添加成功！");
    }

    /***
     * @Description //TODO 删除套餐
     * @param ids
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> deleteSetmeal(Long[] ids){
        setmealService.delete(ids);
        return R.success("删除成功！");
    }

    /***
     * @Description //TODO 查找指定套餐信息，用于数据回显
     * @param id
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @GetMapping("/{id}")
    public R<SetmealDto> findById(@PathVariable("id") Long id){
        SetmealDto setmealDto = setmealService.findSetmealByID(id);
        return R.success(setmealDto);
    }

    /*** 
     * @Description //TODO 更新套餐
     * @param setmealDto 
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PutMapping
    public R<String> updata(@RequestBody SetmealDto setmealDto){
        setmealService.updataSetmeal(setmealDto);
        return R.success("修改成功");
    }

    /*** 
     * @Description //TODO 返回套餐列表
     * @return: com.example.reggie.common.R<java.util.List<com.example.reggie.entity.Setmeal>>
     **/
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#categoryId + '_' + #status")
    public R<List<Setmeal>> list(Long categoryId,Integer status){
        log.info("categoryId:{}",categoryId);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        queryWrapper.and(Wrapper->Wrapper.eq(Setmeal::getStatus,status));
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);

    }

    //停售
    @PostMapping("/status/0")
    public R<String> updataStatus0(Long[] ids){
        //将ids数组转换为list集合
        ArrayList<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);

        //批量将指定id的status修改为0
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,idList).set(Setmeal::getStatus,0);
        setmealService.update(null,updateWrapper);
        return R.success("修改成功");
    }

    //起售
    @PostMapping("/status/1")
    public R<String> updataStatus1(Long[] ids){
        //将ids数组转换为list集合
        ArrayList<Long> idList = new ArrayList<>();
        Collections.addAll(idList,ids);

        //批量将指定id的status修改为1
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,idList).set(Setmeal::getStatus,1);
        setmealService.update(null,updateWrapper);
        return R.success("修改成功");
    }
}

