package com.nowcoder.community.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * ClassName: CookieUtil
 * Package: com.nowcoder.community.community.util
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/28 10:10
 * @Version 1.0
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request,String name){
        if(request == null || name ==null){
            throw new IllegalArgumentException("参数为空！");
        }

        //从客户端的请求中  得到Cookie对象
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
