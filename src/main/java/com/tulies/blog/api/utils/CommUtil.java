package com.tulies.blog.api.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王嘉炀
 * @date 2018/7/10 下午3:40
 */
public class CommUtil {
    public static Sort formatSorter(String sorter){
        try{
            String[] sortstr = sorter.split(" ");
            if ("desc".equals(sortstr[1])) {
                return Sort.by(Sort.Direction.DESC,sortstr[0]);
            }
            return Sort.by(Sort.Direction.ASC,sortstr[0]);
        }catch(Exception e){
            return null;
        }
    }

    /**
     * 自定义分页时的排序字段
     *
     * @param pathBase
     * @param sorter
     * @param <T>
     * @return
     */
    public static <T> OrderSpecifier<?> getSortedColumn(EntityPathBase<T> pathBase, String sorter) {
        try{
            String[] sortstr = sorter.split(" ");
//            if (StringUtils.isAnyBlank(sortstr[0], sortstr[1])) {
//                sort = "desc";
//                field = "createTime";
//            }
            Order order = "asc".equalsIgnoreCase(sortstr[1]) ? Order.ASC : Order.DESC;
            Path<Object> fieldPath = Expressions.path(Object.class, pathBase, sortstr[0]);
            return new OrderSpecifier(order, fieldPath);

        }catch(Exception e){
            return null;
        }
    }

    /**
     * 自定义分页时的排序字段
     *
     * @param pathBase
     * @param sorter
     * @return
     */
    public static List<OrderSpecifier> getSortedColumns(EntityPathBase pathBase, String sorter) {
        try{
            String[]  sortStrArr = sorter.split(",");
            if(sortStrArr.length <=1){
                sortStrArr= new String[]{sorter};
            }
           List<OrderSpecifier> orderSpecifierList = new ArrayList<OrderSpecifier>();
            for (String sort: sortStrArr) {
                String[] sortstr = sort.split(" ");
                Order order = "asc".equalsIgnoreCase(sortstr[1]) ? Order.ASC : Order.DESC;
                Path<Object> fieldPath = Expressions.path(Object.class, pathBase, sortstr[0]);
                orderSpecifierList.add(new OrderSpecifier(order, fieldPath));
            }
            return orderSpecifierList;
        }catch(Exception e){
            return null;
        }
    }
}
