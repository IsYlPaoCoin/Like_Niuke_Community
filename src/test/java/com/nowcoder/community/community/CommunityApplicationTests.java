package com.nowcoder.community.community;


import com.nowcoder.community.community.dao.AlphaDao;
import com.nowcoder.community.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    }

    //测试spring容器
    @Test
    public void testApplicationContext(){
        //证明这个  容器是存在的  可用的
        System.out.println(applicationContext);
        //org.springframework.web.context.support.GenericWebApplicationContext@1613674b, started on Wed Mar 20 19:01:49 CST 2024
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao.select());
        //Hibernate
        //两个Bean 的情况下   ， 加上@Primary注解   优先调用Bean  =>Mybatis

        //其中的一个Bean  命名获取   并强制转型
        alphaDao = applicationContext.getBean("AlphaHibernate",AlphaDao.class);
        System.out.println(alphaDao.select());
        //Hibernate
    }

    //测试Bean的管理方式
    //Bean在容器中只会被  实例化、销毁一次  即是单例的
    @Test
    public void testBeanManagement() {
        //通过容器  获取service
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
        //com.nowcoder.community.community.service.AlphaService@3fdecce
        //加上scope 多例
        //初始化service
        //com.nowcoder.community.community.service.AlphaService@4393593c

        alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
        //com.nowcoder.community.community.service.AlphaService@76c52298
        //加上scope 多例
        //初始化service
        //com.nowcoder.community.community.service.AlphaService@314c8b4a

        // 编写一个配置类  装配三方Bean
    }
    @Test
    public void testBeanConfig() {
        SimpleDateFormat simpleDateFormat =
                applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
        //2024-03-20 19:47:15
    }

    // 当前更简便的  使用方法（通过注入，而不是获取）
    @Autowired
    // 希望包括优先级在内
    @Qualifier("AlphaHibernate")
    private AlphaDao alphaDao;

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    //测试依赖注入
    @Test
    public void testDI(){
        System.out.println(alphaDao);
        //com.nowcoder.community.community.dao.AlphaDaoMyBatisImpl@2c43eb8
        //AlphaHibernate => com.nowcoder.community.community.dao.AlphaDaoHibernateImpl@5cbd159f
        System.out.println(alphaService);
        //com.nowcoder.community.community.service.AlphaService@acf859d
        System.out.println(simpleDateFormat);
        //java.text.SimpleDateFormat@4f76f1a0
    }
}
