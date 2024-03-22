package com.nowcoder.community.community.service;

import com.nowcoder.community.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * ClassName: AlphaService
 * Package: com.nowcoder.community.community.service
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/20 19:20
 * @Version 1.0
 */

@Service
//不希望Bean是单例的  ,当然他的默认参数是singleton
//@Scope("singleton")
//多例 一般情况下不会  这样去使用
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public void AlphaService(){
        System.out.println("实例化AlphaService");
    }

    //这个方法会在  构造器之后调用
    @PostConstruct
    public void init(){
        System.out.println("初始化service");
    }

    //这个方法   在构造器之前调用
    @PreDestroy
    public void destory(){
        System.out.println("销毁service");
    }

    public String find(){
        return alphaDao.select();
    }

}
