package com.nowcoder.community.community.dao;

import org.springframework.stereotype.Repository;

/**
 * ClassName: AlphaDaoHibernateImpl
 * Package: com.nowcoder.community.community.dao
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/20 19:05
 * @Version 1.0
 */
//可以自定义Bean的名称 AlphaHibernate
@Repository("AlphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "Hibernate";
    }
}
