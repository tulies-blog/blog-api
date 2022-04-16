package com.tulies.blog.api.beans.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 王嘉炀
 * @date 2019-10-09 22:07
 */
@Data
public class ArticleDTO {
    @ApiModelProperty(hidden = true)
    private String id;
    @NotBlank(message = "标题必填")
    private String title;
    private String description;
    private String poster;
    @NotBlank(message = "必须选择标签")
    private String tags;
    private Integer status;
    @NotNull(message = "必须选择分类")
    private Integer categoryId;
    @NotBlank(message = "文章正文不能为空")
    private String content;
}
