package com.nowcoder.community.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * ClassName: AlphaAspect
 * Package: com.nowcoder.community.community.aspect
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/6 20:30
 * @Version 1.0
 */

//@Component
//声明为 切面组件
//@Aspect
public class AlphaAspect {

    // 定义 切点
    // service包下的  所有类  、 业务组件 、方法 、所有的参数  都要处理
    @Pointcut("execution(* com.nowcoder.community.community.service.*.*(..))")
    public void pointcut(){
    }

    // 在 连接点 一开始 记录日志
    @Before("pointcut()")
    public void before() {
        System.out.println("test log before");  //no2
    }

    @After("pointcut()")
    public void after() {
        System.out.println("test log after");   //no4
    }

    // 再有了返回值  以后处理
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("test log afterReturning");  //no5
    }

    // 再跑异常的 时候处理
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("test log afterThrowing");
    }

    // 在前后  都植入逻辑
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");  //n01
        Object obj = joinPoint.proceed();
        System.out.println("around after");   //no3
        return obj;
    }
}
