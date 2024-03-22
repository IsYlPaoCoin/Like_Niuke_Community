package com.nowcoder.community.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * ClassName: AlphaDaoMyBatisImpl
 * Package: com.nowcoder.community.community.dao
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/20 19:10
 * @Version 1.0
 */

@Repository
@Primary
public class AlphaDaoMyBatisImpl implements AlphaDao{
    @Override
    public String select() {
        return "Mybatis";
    }
}
