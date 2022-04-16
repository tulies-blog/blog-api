package com.tulies.blog.api.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 王嘉炀
 * @Description:
 * @Date: Created in 1:18 2022/02/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CopyPropertiesConfig {
    //    哪些字段强制不替换
    private String[] ignoreProperties = null;
    //    是否允许null替换
    private Boolean notNull = true;

}