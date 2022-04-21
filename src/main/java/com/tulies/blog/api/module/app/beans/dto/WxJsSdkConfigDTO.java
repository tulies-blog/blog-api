package com.tulies.blog.api.module.app.beans.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ Author     ：王嘉炀
 * @ Date       ：Created in 22:45 2022/4/18
 * @ Description：接受wxJsSdkConfig需要的参数
 */
@ApiModel
@Data
public class WxJsSdkConfigDTO {
    @NotBlank(message = "缺少url参数")
    private String url;
}
