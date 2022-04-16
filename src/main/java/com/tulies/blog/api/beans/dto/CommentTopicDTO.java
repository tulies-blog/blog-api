package com.tulies.blog.api.beans.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: 王嘉炀
 * @Description:
 * @Date: Created in 21:05 2022/02/20
 */
@ApiModel
@Data
public class CommentTopicDTO {
    @ApiModelProperty(hidden = true)
    private Integer id;
    @ApiModelProperty("评论主题id，（如：文章ID）")
    @NotBlank(message = "评论ID必传")
    private String tid;
    @NotBlank(message = "评论主题必传")
    @ApiModelProperty("评论主题")
    private String title;
    @ApiModelProperty("来源URL")
    private String url;
    @ApiModelProperty("评论类型")
    private String type;
}
