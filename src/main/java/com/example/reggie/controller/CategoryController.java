package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.entity.Category;
import com.example.reggie.common.R;
import com.example.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /*** 
     * @Description //TODO 分页查询，用于页面展示数据
     * @param page 
     * @param pageSize
     * @return: com.example.reggie.result.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     **/
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
        
    }

    /*** 
     * @Description //TODO 添加分类
     * @param category 
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PostMapping
    public R<String> AddCategory(@RequestBody Category category){
        if(null==category)
            return R.error("空数据，操作失败");

        boolean flag = categoryService.save(category);
        if(flag==false)
            return R.error("数据库返回false，操作失败");

        return R.success("操作成功！");
    }

    /*** 
     * @Description //TODO 分类菜品修改
     * @param category 
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @PutMapping
    public R<String> updata(@RequestBody Category category){
        if(null==category)
            return R.error("空数据，操作失败");

        boolean flag = categoryService.updateById(category);
        if(flag==false)
            return R.error("数据库返回false，操作失败");

        return R.success("操作成功！");
    }

    /*** 
     * @Description //TODO 根据id进行删除操作
     * @param ids
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @DeleteMapping
    public R<String> DeleteById(long ids){

        categoryService.DeleteById(ids);

        return R.success("操作成功！");
    }

    /***
     * @Description //TODO 获取所有分类数据
     * @return: com.example.reggie.common.R<java.lang.String>
     **/
    @GetMapping("/list")
    public R<List<Category>> categoryList(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        List<Category> categoryList = categoryService.list(queryWrapper);

        return R.success(categoryList);
    }

}

