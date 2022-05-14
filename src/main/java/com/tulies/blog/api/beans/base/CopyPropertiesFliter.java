package com.tulies.blog.api.beans.base;

/**
 * @Author: 王嘉炀
 * @Description:
 * @Date: Created in 15:39 2022/05/03
 */
@FunctionalInterface
public interface CopyPropertiesFliter {
    boolean fliter(String propertyName, Object source, Object target);
}