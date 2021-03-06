package com.tulies.blog.api.handler;

import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.utils.ApiResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常统一处理
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandle {

    /**
     * 404拦截返回
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ApiResult handleResourceNotFoundException(NoHandlerFoundException e) {
        e.printStackTrace();
        return ApiResultUtil.error(ResultEnum.NOT_EXIST.getCode(),ResultEnum.NOT_EXIST.getMessage());
    }


    @ExceptionHandler(value = AppException.class)
    @ResponseBody
    public ApiResult handlerSellerException(AppException e) {
//        e.printStackTrace();
        return ApiResultUtil.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ApiResult handlerMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        e.printStackTrace();
        return ApiResultUtil.error(ResultEnum.PARAM_ERROR.getCode(), ResultEnum.PARAM_ERROR.getMessage()+",缺少"+e.getParameterName());
    }


    /**
     * 全局异常返回
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ApiResult globalExceptionHandler(Exception e){
        e.printStackTrace();
        return ApiResultUtil.error(ResultEnum.SERVER_ERROR.getCode(),ResultEnum.SERVER_ERROR.getMessage());
    }
}
