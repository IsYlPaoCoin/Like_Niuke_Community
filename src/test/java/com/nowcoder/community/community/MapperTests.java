package com.nowcoder.community.community;

import com.nowcoder.community.community.dao.DiscussPostMapper;
import com.nowcoder.community.community.dao.LoginTicketMapper;
import com.nowcoder.community.community.dao.UserMapper;
import com.nowcoder.community.community.entity.DiscussPost;
import com.nowcoder.community.community.entity.LoginTicket;
import com.nowcoder.community.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

//    @Autowired
//    private MessageMapper messageMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);
        //User{id=101, username='liubei', password='390ba5f6b5f18dd4c63d7cda170a0c74', salt='12345', email='nowcoder101@sina.com', type=0, status=1, activationCode='null', headerUrl='http://images.nowcoder.com/head/100t.png', createTime=Wed Apr 03 12:04:55 CST 2019}

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);
        //1

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);
        //1

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
        //1
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149 , 0, 10,0);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        // 10min 以后就 到期了
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        //LoginTicket{id=1, userId=101, ticket='abc', status=0, expired=Wed Mar 27 19:28:59 CST 2024}
        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        //LoginTicket{id=1, userId=101, ticket='abc', status=1, expired=Wed Mar 27 19:20:36 CST 2024}
    }
//
//    @Test
//    public void testSelectLetters() {
//        List<Message> list = messageMapper.selectConversations(111, 0, 20);
//        for (Message message : list) {
//            System.out.println(message);
//        }
//
//        int count = messageMapper.selectConversationCount(111);
//        System.out.println(count);
//
//        list = messageMapper.selectLetters("111_112", 0, 10);
//        for (Message message : list) {
//            System.out.println(message);
//        }
//
//        count = messageMapper.selectLetterCount("111_112");
//        System.out.println(count);
//
//        count = messageMapper.selectLetterUnreadCount(131, "111_131");
//        System.out.println(count);
//
//    }
}
