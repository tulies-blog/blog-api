package com.tulies.blog.api.beans.base;

import lombok.Data;

/**
 * @author 王嘉炀
 * @date 2019/8/19 下午1:29
 */
@Data
public class ApiResult<T> {
    private Integer code;
    private String message;
    private T data;
}
