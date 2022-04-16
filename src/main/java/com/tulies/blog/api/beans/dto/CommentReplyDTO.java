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
public class CommentReplyDTO {
    @ApiModelProperty(hidden = true)
    private Integer id;
    @NotBlank(message = "评论ID必传")
    private String tid;
    @NotBlank(message = "缺少评论内容")
    private String content;
    private Integer grade;
    // 审核状态
    private Integer checkStatus;
    private Integer status;

    private String userid;
    @NotBlank(message = "缺少用户名")
    private String username;
    // 用户邮箱
    private String email;
    private String website;

    private Integer parentid;
    private Integer rootid;
}
