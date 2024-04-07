package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.entity.Comment;
import com.nowcoder.community.community.entity.DiscussPost;
import com.nowcoder.community.community.entity.Page;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.CommentService;
import com.nowcoder.community.community.service.DiscussPostService;
import com.nowcoder.community.community.service.UserService;
import com.nowcoder.community.community.util.CommunityConstant;
import com.nowcoder.community.community.util.CommunityUtil;
import com.nowcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * ClassName: DiscussPostController
 * Package: com.nowcoder.community.community.controller
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/3 11:18
 * @Version 1.0
 */

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService dicussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add" ,method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"您还没有登录哦！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());

        dicussPostService.addDicusspost(post);

        // 报错的情况，将来统一处理
        return CommunityUtil.getJSONString(201,"发布成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDicussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        // 1. 帖子
        DiscussPost post = dicussPostService.findDiscussPostById(discussPostId);
        // 将参数传递给 模板
        model.addAttribute("post",post);
        // 2. 作者
        // 如果一个表中的信息 不够查！
        // 法1： 通过sql 进行 关联查询
        // ==》 缺点: 是有可能  只有这一个业务需求  的mapper 需要这种方法,  其他的业务用这个 mapper可能会有  冗余的情况
        // 法2： 通过帖子对象 拿到userId  再去获取user信息
        // ==> 缺点:相比而言  需要查询两次  效率可能会  比较低
        // ===> solution: 解决方法是 将有影响效率较大的数据  缓存到redis  从内存中取值  影响会很小
        // ===> 代码会变得相对简洁  不会冗余一些数据   性能也能够得到提升
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        // 评论分页 信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());

        // 评论： 给帖子的评论
        // 回复： 给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit());
        // 评论Vo列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment : commentList){
                // map=> 评论列表
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment",comment);
                // 作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                // 回复 列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(),0,Integer.MAX_VALUE);
                // 回复 VO 列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for (Comment reply: replyList) {
                        Map<String,Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }
}
