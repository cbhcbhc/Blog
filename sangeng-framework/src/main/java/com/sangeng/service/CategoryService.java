package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.vo.CategoryVo;
import com.sangeng.domain.vo.PageVo;

import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2022-09-26 09:18:43
 */
public interface CategoryService extends IService<Category> {
    /**
     * 3.2 查询分类列表
     * @return
     */
    ResponseResult getCategoryList();


    ResponseResult listAllCategory();

    PageVo selectCategoryPage(Category category, Integer pageNum, Integer pageSize);
}

