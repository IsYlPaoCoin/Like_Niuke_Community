package com.nowcoder.community.community.controller.interceptor;

import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.MessageService;
import com.nowcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName: MessageInterceptor
 * Package: com.nowcoder.community.community.controller.interceptor
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/15 19:56
 * @Version 1.0
 */

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView !=null) {
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId() ,null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}
