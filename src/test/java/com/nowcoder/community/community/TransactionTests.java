package com.nowcoder.community.community;

import com.nowcoder.community.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ClassName: TransactionTests
 * Package: com.nowcoder.community.community
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/4 16:07
 * @Version 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1(){
        Object obj = alphaService.save();
        System.out.println(obj);
    }

    @Test
    public void testSave2(){
        Object obj = alphaService.save();
        System.out.println(obj);
    }
}

























