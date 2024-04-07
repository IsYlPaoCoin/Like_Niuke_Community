package com.nowcoder.community.community.controller.interceptor;

import com.nowcoder.community.community.annotation.LoginRequired;
import com.nowcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.handler.Handler;
import java.lang.reflect.Method;

/**
 * ClassName: LoginRequiredInterceptor
 * Package: com.nowcoder.community.community.controller.interceptor
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/30 16:17
 * @Version 1.0
 */

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否登录
        //1.首先判断拦截的 目标是不是方法
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //  获取他拦截的对象
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //  判断这个方法是否需要登录 访问 + 用户是否登录
            if(loginRequired != null && hostHolder.getUser() == null){
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}













