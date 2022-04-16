package com.tulies.blog.api.module.app.beans.dto;

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
public class AppCommentReplyDTO {
    @ApiModelProperty(hidden = true)
    private Integer id;
    @NotBlank(message = "评论ID必传")
    private String tid;
    @NotBlank(message = "评论内容")
    private String content;
//    @NotBlank(message = "被回复的评论ID")
    private Integer parentid;
    private String userid;
    @NotBlank(message = "评论用户名称必传")
    private String username;
    // 用户邮箱
    private String email;
    // 用户个人主页
    private String website;
}
