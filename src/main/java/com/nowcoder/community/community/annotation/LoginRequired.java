package com.nowcoder.community.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: LoginRequired
 * Package: com.nowcoder.community.community.annotation
 * Description:
 * @Author 杨理
 * @Create 2024/3/30 16:07
 * @Version 1.0
 */

// 是否需要登录
@Target(ElementType.METHOD)   // 标识这个 注解 可以使用在 方法上边
@Retention(RetentionPolicy.RUNTIME)   // 标识 这个注解 在运行时 有效
public @interface LoginRequired {

}
