package com.nowcoder.community.community.dao.elasticsearch;

import com.nowcoder.community.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ClassName: DiscussPostRepository
 * Package: com.nowcoder.community.community.dao.elasticsearch
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/26 9:41
 * @Version 1.0
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
