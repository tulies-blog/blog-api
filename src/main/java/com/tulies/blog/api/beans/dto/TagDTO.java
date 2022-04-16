package com.tulies.blog.api.beans.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class TagDTO {
    @ApiModelProperty(hidden = true)
    private Integer id;
    @NotBlank(message = "标签名称必填")
    private String name;
    private String description;
    private String cover;
    private Integer status;
    private Integer sort;
    private Date createTime;
}
