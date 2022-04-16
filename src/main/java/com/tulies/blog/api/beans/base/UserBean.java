package com.tulies.blog.api.beans.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 王嘉炀
 * @Description:
 * @Date: Created in 19:35 2022/04/05
 */
@Data
public class UserBean implements Serializable {
    private static final long serialVersionUID = 8785138549708742519L;


    @ApiModelProperty(hidden = true)
    private String uid;

    @ApiModelProperty(hidden = true)
    private String userName;

}
