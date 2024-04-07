package com.nowcoder.community.community.service;

import com.nowcoder.community.community.dao.AlphaDao;
import com.nowcoder.community.community.dao.DiscussPostMapper;
import com.nowcoder.community.community.dao.UserMapper;
import com.nowcoder.community.community.entity.DiscussPost;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

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

    // 选择一个默认合理的方式
    // @Transactional
    // REQUIRED      ->   支持当前事务（外部事务）  ， 如果不存在则创建新事务
    // REQUIRES_NEW   ->   创建一个新事物，  并暂停当前事务（外部事务）
    // NESTED  ->   如果存在事务（外部事务），   则嵌套在该事务中执行（独立的提交和回滚，如果不存在则创建新事务）
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save() {
        //新增用户
        User user = new User();
        user.setUsername("drl");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("drl@qq.com");
        user.setHeaderUrl("http://image.nowcode.com/head/99.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setTitle("welcome  new person  Hello");
        post.setContent("new person 报道");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");

        return "ok";
    }

    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcode.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setTitle("welcome  beta new person  Hello");
                post.setContent("new beta person 报道");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");
                return "ok";
            }
        });
    }
}
