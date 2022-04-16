package com.tulies.blog.api.exception;

import com.tulies.blog.api.enums.ResultEnum;
import lombok.Getter;

/**
 * @author 王嘉炀
 * @date 2018/6/30 下午4:15
 */
@Getter
public class AppException extends RuntimeException {

    private Integer code;

    public AppException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
    public AppException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
