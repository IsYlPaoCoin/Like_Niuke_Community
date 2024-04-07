package com.nowcoder.community.community.controller.interceptor;

import com.nowcoder.community.community.entity.LoginTicket;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.UserService;
import com.nowcoder.community.community.util.CookieUtil;
import com.nowcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * ClassName: LoginTicketInterceptor
 * Package: com.nowcoder.community.community.interceptor
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/28 10:06
 * @Version 1.0
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从 cookie 中获取凭证
        String ticket = CookieUtil.getValue(request,"ticket");

        if(ticket != null){
            // 查询 凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 判断 凭证是否 有效
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                // 根据凭证查询用户
                User user =userService.findUserById(loginTicket.getUserId());
                // 让本次请求 持有用户 ， 即 随时可以 拿得到
                // 用工具 去 持有用户 ， 把数据存入了 当前线程的 map 中  ，只要这个请求 没有 处理完 ，这个线程 就一直 都在
                // 当请求处理完  服务器向浏览器  做出响应后  这个线程会被销毁
                // =》所以说 请求在处理中时  线程一直处于存活状态  local中的 数据一直都在
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    //是在调用controller完  之后执行的
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        hostHolder.clear();
    }
}







