package com.nowcoder.community.community.aspect;

import lombok.extern.apachecommons.CommonsLog;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClassName: ServiceLogAspect
 * Package: com.nowcoder.community.community.aspect
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/6 20:56
 * @Version 1.0
 */

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    // 定义 切点
    // service包下的  所有类  、 业务组件 、方法 、所有的参数  都要处理
    @Pointcut("execution(* com.nowcoder.community.community.service.*.*(..))")
    public void pointcut(){
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户ip【x.x.x.x】在【2024-04-06】，访问了【com.nowcoder.community.community.service.* 方法】
        // 获取用户ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        // 获取指定格式  的日期
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 得到目标类型名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." +joinPoint.getSignature().getName();

        logger.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));
    }

}
