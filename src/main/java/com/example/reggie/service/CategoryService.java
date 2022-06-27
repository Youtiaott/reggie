package com.example.reggie.service;

import com.example.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
public interface CategoryService extends IService<Category> {
    public void DeleteById(long ids);
}
