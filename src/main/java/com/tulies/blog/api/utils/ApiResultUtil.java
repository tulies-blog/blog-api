package com.tulies.blog.api.utils;


import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.enums.ResultEnum;

/**
 * @author 王嘉炀
 * @date 2019/6/30 下午1:58
 */
public class ApiResultUtil<T> {
    public static ApiResult success(Object object){
        return info(0, "成功", object);
    }
    public static ApiResult success(Object object,String msg){
        return info(0, msg, object);
    }
    public static ApiResult success(){
        return success(null);
    }

    public static <T> ApiResult<T> ok(T data){
        ApiResult<T> resultVO = new ApiResult<>();
        resultVO.setCode(0);
        resultVO.setMessage("成功");
        resultVO.setData(data);
        return resultVO;
    }


    public static ApiResult error(Integer code, String msg){
        ApiResult resultVO = new ApiResult();
        resultVO.setCode(code);
        resultVO.setMessage(msg);
        return resultVO;
    }
    public static ApiResult error(ResultEnum resultEnum){
        ApiResult resultVO = new ApiResult();
        resultVO.setCode(resultEnum.getCode());
        resultVO.setMessage(resultEnum.getMessage());
        return resultVO;
    }

    // 全部自定义
    public static ApiResult info(Integer code, String msg , Object object){
        ApiResult resultVO = new ApiResult();
        resultVO.setCode(code);
        resultVO.setMessage(msg);
        resultVO.setData(object);
        return resultVO;
    }

    public static ApiResult info(Integer code, String msg){
        ApiResult resultVO = new ApiResult();
        resultVO.setCode(code);
        resultVO.setMessage(msg);
        return resultVO;
    }
}
