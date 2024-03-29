package com.nowcoder.community.community.util;

import com.nowcoder.community.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * ClassName: HostHolder
 * Package: com.nowcoder.community.community.util
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/28 10:31
 * @Version 1.0
 */

/**
 * 起到容器的作用，
 * 持有用户的信息 ，（即用于代替session对象）
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<User>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
