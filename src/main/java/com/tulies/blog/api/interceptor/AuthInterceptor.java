package com.tulies.blog.api.interceptor;

import com.tulies.blog.api.annotation.IgnoreSecurity;
import com.tulies.blog.api.beans.vo.UserVO;
import com.tulies.blog.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author 王嘉炀
 * @date 2018/7/15 下午5:30
 */
@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    //在请求处理之前进行调用（Controller方法调用之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        String requestPath = request.getRequestURI();
        //   log.debug("requestIp: " + getIpAddress(request));
        log.info("Method: " + method.getName() + ", IgnoreSecurity: " + method.isAnnotationPresent(IgnoreSecurity.class));
        log.info("requestPath: " + requestPath);
//        if (requestPath.contains("/v2/api-docs") || requestPath.contains("/swagger") || requestPath.contains("/configuration/ui")) {
//            return true;
//        }
        if (method.isAnnotationPresent(IgnoreSecurity.class)) {
            return true;
        }

        String ssotoken = request.getHeader("ssotoken");
        if (StringUtils.isBlank(ssotoken)) {
            log.error("Invalid Token. token 不存在 ");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token 不存在.");
            return false;
        }
//        String token = ssotoken.split(" ")[1];

        UserVO userVO = userService.queryUserInfo(ssotoken);
        if (userVO == null) {
            log.error("Invalid Token.token 无效.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token 无效");
            return false;
        }
        request.setAttribute("currentUser", userVO);
        return true;
    }

    //请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
