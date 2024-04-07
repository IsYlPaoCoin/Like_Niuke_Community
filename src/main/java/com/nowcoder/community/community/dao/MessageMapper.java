package com.nowcoder.community.community.dao;

import com.nowcoder.community.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: MessageMapper
 * Package: com.nowcoder.community.community.dao
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/5 15:56
 * @Version 1.0
 */

@Mapper
public interface MessageMapper {
    // 1. 查询当前用户  的会话列表  ， 针对每个会话  只返回一条最新的 私信
    List<Message> selectConversations(int userId,int offset,int limit);

    // 2.查询当前用户  的会话数量
    int selectConversationCount(int  userId);

    // 3.查询某个会话 所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 4.查询某个会话  所包含的 私信数量
    int selectLetterCount(String conversationId);

    // 5.查询 未读私信的数量
    int selectLetterUnreadCount(int userId, String conversationId);

    // 6.增加 一条消息 方法
    int insertMessage(Message message);

    // 7.更改 私信状态  方法
    int updateStatus(List<Integer> ids, int status);
}
