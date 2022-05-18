package com.tulies.blog.api.utils;

import com.tulies.blog.api.beans.base.CopyPropertiesConfig;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: 王嘉炀
 * @Description:
 * @Date: Created in 1:17 2022/02/22
 */
public class BeanUtil {
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, new CopyPropertiesConfig());
    }
    // 主要为了快捷的设置不对null值进行复制
    public static void copyProperties(Object source, Object target, boolean ignoreNull ) {
        copyProperties(source, target, new CopyPropertiesConfig().setIgnoreNull(ignoreNull));
    }
    public static void copyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
        copyProperties(source, target, new CopyPropertiesConfig().setIgnoreProperties(ignoreProperties));
    }
    public static void copyProperties(Object source, Object target, CopyPropertiesConfig copyPropertiesConfig) {
        String[] ignoreProperties = getIgnoreProperties(source, target, copyPropertiesConfig);
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }
    // 根据 CopyPropertiesConfig 获取需要忽略的属性
    private static String[] getIgnoreProperties(Object source, Object target, CopyPropertiesConfig config) {
        Set<String> ignoreSet =new HashSet<String>();
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        // 判断fliter是否为null，不为null，则去逐条判断
        for (PropertyDescriptor pd : pds) {
            if (config.getIgnoreNull() && src.getPropertyValue(pd.getName()) == null) {
                // 字段不允许为空
                ignoreSet.add(pd.getName());
            }else if(config.getCopyPropertiesFliter()!=null && config.getCopyPropertiesFliter().fliter(pd.getName(),source,target)){
                // 走自定义过滤方法处理
                ignoreSet.add(pd.getName());
            }
        }
        if (config.getIgnoreProperties() != null) {
            ignoreSet.addAll(new HashSet<String>(Arrays.asList(config.getIgnoreProperties())));
        }
        String[] result = new String[ignoreSet.size()];
        return ignoreSet.toArray(result);
    }
}
