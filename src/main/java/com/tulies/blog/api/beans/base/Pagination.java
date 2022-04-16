package com.tulies.blog.api.beans.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 王嘉炀
 * @date 2018/8/19 上午10:44
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination<T> {

    private List<T> list;
    private Integer pageNum;
    private Integer pageSize;
    private Long total;

    public static <S> Pagination<S> create (List<S> list, Integer pageNum, Integer pageSize, Long total){
        return new Pagination<S>(list,pageNum,pageSize,total);
    }
}
