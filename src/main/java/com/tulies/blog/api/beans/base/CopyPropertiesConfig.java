package com.tulies.blog.api.beans.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: 王嘉炀
 * @Description:
 * @Date: Created in 1:18 2022/02/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CopyPropertiesConfig {
    // 哪些字段强制不替换
    private String[] ignoreProperties = null;
    // 是否允许null替换
    private Boolean allowNull = true;
    // 自定义过滤器，fliter方法返回true标识需要过滤
    private CopyPropertiesFliter copyPropertiesFliter;
}

