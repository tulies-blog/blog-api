package com.tulies.blog.api.service;

import com.tulies.blog.api.beans.vo.CategoryNodeVO;
import com.tulies.blog.api.entity.Category;

import java.util.List;
import java.util.Map;

/**
 * @author 王嘉炀
 * @date 2019-10-12 00:07
 */
public interface CategoryService {

    //    PageVO<Category> findList(Integer pageNum, Integer pageSize, ResourceQo resourceQo);
    List<Category> findAll();

    List<CategoryNodeVO> tree();

    List<Map<String, Object>> articleCount();

    Category findByUrlName(String urlName);

    Category findById(Integer id);

    void updateArticleCount(Integer id);
}
