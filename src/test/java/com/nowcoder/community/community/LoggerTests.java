package com.nowcoder.community.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author shuaisen ma
 * @version 1.0.0
 * @ClassName LoggerTests.java
 * @Description Log test
 * @createTime 2020年04月28日 13:14:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {
    private static  final Logger logger= LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger() {
        System.out.println(logger.getName());
        //com.nowcoder.community.community.LoggerTests

        //如果把日志级别设置为warn的话  只会打印 warn + error
        logger.debug("debug log");
        //2024-03-22 12:43:37.796 DEBUG 15804 --- [           main] c.n.community.community.LoggerTests      : debug log
        //配置后的日志   更加简短  =》2024-03-22 17:09:37,888 DEBUG [main] c.n.c.c.LoggerTests [LoggerTests.java:30] debug log
        logger.info("info log");
        //2024-03-22 12:43:37.796  INFO 15804 --- [           main] c.n.community.community.LoggerTests      : info log
        logger.warn("warn log");
        logger.error("error log");
    }

//    测试git提交
}
