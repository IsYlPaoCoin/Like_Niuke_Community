package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.entity.Event;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.event.EventProducer;
import com.nowcoder.community.community.service.LikeService;
import com.nowcoder.community.community.util.CommunityConstant;
import com.nowcoder.community.community.util.CommunityUtil;
import com.nowcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: LikeController
 * Package: com.nowcoder.community.community.controller
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/10 11:00
 * @Version 1.0
 */

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);

        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType,entityId);

        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        // 触发点赞事件
        if(likeStatus == 1){
            Event event = new Event();

            event.setTopic(TOPIC_LIKE);
            event.setUserId(hostHolder.getUser().getId());
            event.setEntityType(entityType);
            event.setEntityUserId(entityUserId);
            event.setData("postId",postId);

        eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(0,null,map);
    }

}
