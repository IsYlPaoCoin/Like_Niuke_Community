package com.nowcoder.community.community.dao;

import com.nowcoder.community.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: CommentMapper
 * Package: com.nowcoder.community.community.dao
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/4 17:12
 * @Version 1.0
 */

@Mapper
public interface CommentMapper {

    // 查 指定要求  的评论
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    // 查 指定类型  评论的数量
    int selectCountByEntity(int entityType, int entityId);

    // 添加 评论
    int insertComment(Comment comment);
}
