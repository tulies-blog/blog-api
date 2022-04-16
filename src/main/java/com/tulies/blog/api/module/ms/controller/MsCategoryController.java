package com.tulies.blog.api.module.ms.controller;

import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.service.CategoryService;
import com.tulies.blog.api.utils.ApiResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王嘉炀
 * @date 2019-10-12 00:20
 */
@Api(tags = "分类管理")
@RestController
@RequestMapping("/ms/categorys")
public class MsCategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "分类查询")
    @GetMapping
    public ApiResult all(){
        return ApiResultUtil.success( this.categoryService.findAll() );
    }


    @ApiOperation(value = "分类树")
    @GetMapping("/tree")
    public ApiResult tree(){
        return ApiResultUtil.success( this.categoryService.tree() );
    }
}
