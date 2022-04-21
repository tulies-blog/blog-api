package com.tulies.blog.api.module.app.controller;

import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.module.app.beans.dto.WxJsSdkConfigDTO;
import com.tulies.blog.api.utils.ApiResultUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @ Author     ：王嘉炀
 * @ Date       ：Created in 21:45 2022/4/18
 * @ Description：微信的sdk接口
 */
@Slf4j
@Api(tags = "微信模块")
@RestController
@RequestMapping("/app/wx")
public class AppWxController {

    @Autowired
    private WxMpService wxMpService;

    @PostMapping("/wxJsapiSignature")
    public ApiResult<WxJsapiSignature> wxJsSdkConfig(@RequestBody @Valid WxJsSdkConfigDTO wxJsSdkConfigDTO, BindingResult bindingResult) throws WxErrorException {
        if (bindingResult.hasErrors()) {
            throw new AppException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        // this.mpService.getWxMpConfigStorage().getAppId();
        WxJsapiSignature wxJsapiSignature = this.wxMpService.createJsapiSignature(wxJsSdkConfigDTO.getUrl());
        return ApiResultUtil.success(wxJsapiSignature);
    }

}
