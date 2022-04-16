package com.tulies.blog.api.converter;


import com.tulies.blog.api.beans.base.Pagination;
import org.springframework.data.domain.Page;

/**
 * @author 王嘉炀
 * @date 2019/8/20 上午9:19
 */
public class PageResultConverter {
    public static Pagination convert(Page page){
        Pagination pagination = new Pagination();
        pagination.setPageNum(page.getPageable().getPageNumber()+1);
        pagination.setPageSize(page.getPageable().getPageSize());
        pagination.setTotal(page.getTotalElements());
        pagination.setList(page.getContent());
        return pagination;
    }
}
