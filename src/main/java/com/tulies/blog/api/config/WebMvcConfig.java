package com.tulies.blog.api.config;

import com.tulies.blog.api.interceptor.AuthInterceptor;
import com.tulies.blog.api.interceptor.CurrentUserMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;

/**
 * @author 王嘉炀
 * @date 2018/7/15 下午5:37
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private BaseConfigProperties baseConfigProperties;
    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/stc/**").addResourceLocations("file:" + baseConfigProperties.getUploadPathPrefix() + File.separator);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        if (baseConfigProperties.isOpenAuth()) {
//        List<String> excludeList = new ArrayList<String>();
//        excludeList.add("/user/**");
//        excludeList.add("/act/user/**");

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/ms/**");
//                .excludePathPatterns(excludeList);

//        registry.addInterceptor(permissionInterceptor)
////                .addPathPatterns("/**")
//                .excludePathPatterns(excludeList);
//        }
    }

    // 参数级别的
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(currentUserMethodArgumentResolver());
    }

    @Bean
    public CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver() {
        return new CurrentUserMethodArgumentResolver();
    }

}
