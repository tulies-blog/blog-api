package com.tulies.blog.api.interceptor;

import com.tulies.blog.api.annotation.CurrentUser;
import com.tulies.blog.api.beans.base.UserBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author 王嘉炀
 * @date 2018/7/17 下午10:34
 */
@Slf4j
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(UserBean.class)
                && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authid = webRequest.getHeader("authid");
        UserBean userInfo = (UserBean) webRequest.getAttribute("currentUser", RequestAttributes.SCOPE_REQUEST);
        if (userInfo != null) {
            return userInfo;
        }
        // todo 下面这个是模拟的数据
        userInfo = new UserBean();
        userInfo.setUid(authid);
        return userInfo;

//        log.error("缺少currentUser");
//        throw new MissingServletRequestPartException("currentUser");
    }
}
