package com.nowcoder.community.community.service;

import com.nowcoder.community.community.dao.CommentMapper;
import com.nowcoder.community.community.dao.DiscussPostMapper;
import com.nowcoder.community.community.entity.Comment;
import com.nowcoder.community.community.util.CommunityConstant;
import com.nowcoder.community.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * ClassName: CommentService
 * Package: com.nowcoder.community.community.service
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/4 17:40
 * @Version 1.0
 */

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    // 查 评论实体 详情
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset,int limit) {
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    // 查 评论数量
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    // 处理 增加评论
    // 由于当前方式是在一个事务  之内， 并不是在 局部  =>  选用声明式事务  会好一些
    // 隔离级别 、 传播机制
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 对评论 中的内容进行  敏感词过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        // 插入 评论
        int rows = commentMapper.insertComment(comment);

        // 更新 评论数量  =》只有更新帖子的时候  ， 才会更新帖子的评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return rows;
    }

    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }


}
