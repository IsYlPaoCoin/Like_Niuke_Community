package com.nowcoder.community.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName: AlphaInterceptor
 * Package: com.nowcoder.community.community.interceptor
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/28 9:14
 * @Version 1.0
 */
@Component
public class AlphaInterceptor implements HandlerInterceptor {
    //这里需要打印日志，  实例化日志
    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);

    @Override
    //拦截请求的时候，
    //是在controller请求  之前执行的
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle: "+handler.toString());
        //preHandle: public java.lang.String com.nowcoder.community.community.controller.LoginController.getRegisterPage()
        return true;
    }

    //是在调用controller完  之后执行的
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle: "+handler.toString());
        //postHandle: public java.lang.String com.nowcoder.community.community.controller.LoginController.getRegisterPage()
    }

    //在模板引擎  执行完后  执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        logger.debug("afterCompletion: "+handler.toString());
        //afterCompletion: public java.lang.String com.nowcoder.community.community.controller.LoginController.getRegisterPage()
    }

}
