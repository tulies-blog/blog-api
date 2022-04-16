package com.tulies.blog.api.module.ms.controller;

import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.beans.qo.UserQO;
import com.tulies.blog.api.entity.User;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.service.UserService;
import com.tulies.blog.api.utils.ApiResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王嘉炀
 * @date 2019-10-13 15:45
 */
@Api(tags = "用户管理")
@Slf4j
@RestController
@RequestMapping("/ms/users")
public class MsUserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户列表")
    @GetMapping
    public ApiResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                          UserQO userQO,
                          String sorter) {
        Pagination<User> pageVO = this.userService.findList(pageNum - 1, pageSize, userQO, sorter);
        return ApiResultUtil.success(pageVO);
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{id}")
    public ApiResult delete(@PathVariable Integer id) {
        // 先查询下当前这个活动信息
        User record = userService.findById(id);
        if (record == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        userService.deleteById(id);
        return ApiResultUtil.success();
    }

    @ApiOperation(value = "更新用户状态")
    @PatchMapping("/{id}/status/{status}")
    public ApiResult changeStatus(@PathVariable Integer id, @PathVariable Integer status) {
        // 先查询下当前这个活动信息，判断下状态，是否是可以删除的情况。
        User user = userService.findById(id);
        if (user == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        userService.changeStatus(id, status);
        return ApiResultUtil.success();
    }

}
