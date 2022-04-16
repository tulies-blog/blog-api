package com.tulies.blog.api.beans.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: 王嘉炀
 * @Description:
 * @Date: Created in 15:43 2022/04/05
 */
@Data
public class ArticleInteractDTO {
    @NotBlank(message = "内容ID不能为空")
    private String articleId;
    @NotBlank(message = "用户ID不能为空")
    private String uid;
    private Integer isDigg = 0;
    private Integer isVisit = 0;
}
