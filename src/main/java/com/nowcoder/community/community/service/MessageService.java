package com.nowcoder.community.community.service;

import com.nowcoder.community.community.dao.MessageMapper;
import com.nowcoder.community.community.entity.Message;
import com.nowcoder.community.community.util.SensitiveFilter;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * ClassName: MessageService
 * Package: com.nowcoder.community.community.service
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/5 17:38
 * @Version 1.0
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    // 1. (分页)查询当前用户  的会话列表
    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    // 2. 查询 当前用户的 会话数量
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    // 3.（分页）查询当前会话 的消息列表
    public List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    // 4. 查询某个会话  所包含的 私信数量
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    // 5. 查询  某个用户 未读私信的数量
    public int findLetterUnreadCount(int userId,String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    // 6. 新增  一条消息
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    // 7. 已读 一条消息
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }


    // 8.查询 某一个主题下 的 最新通知
    public Message findLatestNotice(int userId, String topic){
        return messageMapper.selectLatestNotice(userId, topic);
    }


    // 9.查询 某个主题下 最新通知的 数量
    public int findNoticeCount(int userId, String topic){
        return messageMapper.selectNoticeCount(userId, topic);
    }

    // 10.查询 未读的 通知数量
    public int findNoticeUnreadCount(int userId, String topic){
        return messageMapper.selectLetterUnreadCount(userId,topic);
    }

    // 11.查询某个主题  包含的通知列表
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId,topic,offset,limit);
    }
}
