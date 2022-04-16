package com.tulies.blog.api.module.ms.controller;

import com.tulies.blog.api.annotation.IgnoreSecurity;
import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.beans.dto.LoginDTO;
import com.tulies.blog.api.beans.vo.UserVO;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.service.UserService;
import com.tulies.blog.api.utils.ApiResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王嘉炀
 * @date 2019-10-13 15:45
 */
@Api(tags = "登录管理")
@Slf4j
@RestController
@RequestMapping("/ms/login")
public class MsLoginController {

    @Autowired
    private UserService userService;

    @IgnoreSecurity
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public ApiResult login(@RequestBody LoginDTO loginDTO) {
        if (StringUtils.isBlank(loginDTO.getUsername()) || StringUtils.isBlank(loginDTO.getPassword())) {
            throw new AppException(ResultEnum.PARAM_ERROR.getCode(),
                    ResultEnum.PARAM_ERROR.getMessage() + ",缺少帐号或密码");
        }
        UserVO userVO = this.userService.login(loginDTO);
        return ApiResultUtil.success(userVO);
    }
}
