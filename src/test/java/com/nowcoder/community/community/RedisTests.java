package com.nowcoder.community.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: RedisTests
 * Package: com.nowcoder.community.community
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/7 11:57
 * @Version 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;

    // String类型  的  访问测试
    @Test
    public void testString() {
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        // 1
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        // 2
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
        // 1
    }

    // hash类型  的  使用测试
    @Test
    public void testHashes() {
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","phdvb");

        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        // 1
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
        // phdvb
    }

    // 列表类型 的使用 测试
    @Test
    public void testLists() {
        String redisKey = "test:ids";

        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);
        redisTemplate.opsForList().leftPush(redisKey,104);
        redisTemplate.opsForList().leftPush(redisKey,105);
        redisTemplate.opsForList().leftPush(redisKey,106);

        // 106 105 104 103 102 101

        System.out.println(redisTemplate.opsForList().size(redisKey));
        // 6
        System.out.println(redisTemplate.opsForList().index(redisKey,2));
        // 104
        System.out.println(redisTemplate.opsForList().range(redisKey,2,4));
        // [104, 103, 102]
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        // 106
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        // 105
        System.out.println(redisTemplate.opsForList().size(redisKey));
        // 4
    }

    // 集合类型  的使用测试
    @Test
    public void testSets() {
        String redisKey = "test:teachers";

        redisTemplate.opsForSet().add(redisKey,"hf","yf","hw","tb","xq","yl");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        // 6
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        // yl
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        // tb
        System.out.println(redisTemplate.opsForSet().members(redisKey));
        //[hw, xq, hf, yf]
    }

    // 有序集合
    @Test
    public void testSortedSets() {
        String redisKey = "test:students";
        redisTemplate.opsForZSet().add(redisKey,"phdvb",80);
        redisTemplate.opsForZSet().add(redisKey,"phdvc",90);
        redisTemplate.opsForZSet().add(redisKey,"phdvd",70);
        redisTemplate.opsForZSet().add(redisKey,"phdve",30);
        redisTemplate.opsForZSet().add(redisKey,"phdvf",50);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        //5
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"phdvc"));
        //90
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"phdvd"));
        //排名
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,2));
        //[phdvc, phdvb, phdvd]
    }

    // other
    @Test
    public void testKeys() {
        System.out.println(redisTemplate.hasKey("test:user"));
        //  true
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        //  false

        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);
        System.out.println(redisTemplate.hasKey("test:students"));
        //  false
    }

    // 多次访问  同一个key
    @Test
    public void testBoundOperations() {
        String redisKey = "testCount";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
        //  5
    }

    // redis事务机制  ==》  编程式事务
    // 当启用事务以后  再去执行一个redis命令时
    // 并不会立刻执行这个命令  而是把这个命令放在队列里
    // 直到提交事务后     会将队列里的所有命令一同发给redis   服务器执行
    @Test
    public void testTransactional() {
        // 方法内  需要传一个实例  进行匿名实现
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //在这个 方法内  演示事务
                String redisKey = "test:tx";

                // 启用事务  关键（1）
                operations.multi();

                operations.opsForSet().add(redisKey,"zhangsan");
                operations.opsForSet().add(redisKey,"lisi");
                operations.opsForSet().add(redisKey,"wangwu");

                System.out.println(operations.opsForSet().members(redisKey));
                // []

                // 关键（2）
                return operations.exec();
                // [1, 1, 1, [lisi, wangwu, zhangsan]]
            }
        });
        System.out.println(obj);
    }
}
