package com.nowcoder.community.community;

import com.nowcoder.community.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ClassName: Sensitive
 * Package: com.nowcoder.community.community
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/1 18:36
 * @Version 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以赌博，可以嫖娼，可以吸毒，可以开票";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
        //这里可以赌博，可以***，可以***，可以***
        String text1 = "这里可以赌→博，可以嫖#娼，可以吸→毒，可以开→票";
        text1 = sensitiveFilter.filter(text1);
        System.out.println(text1);
        //这里可以赌→博，可以***，可以***，可以***
        String text2 = "这里可以%%赌→博￥，可以#（嫖#娼），可以@@吸→毒@@，可以&开→票！";
        text2 = sensitiveFilter.filter(text2);
        System.out.println(text2);
        //这里可以%%赌→博￥，可以#（***），可以@@***@@，可以&***！
    }
}
