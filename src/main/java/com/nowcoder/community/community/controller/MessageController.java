package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.entity.Message;
import com.nowcoder.community.community.entity.Page;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.MessageService;
import com.nowcoder.community.community.service.UserService;
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
 * ClassName: MessageController
 * Package: com.nowcoder.community.community.controller
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/5 17:54
 * @Version 1.0
 */

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 01. 私信列表
    @RequestMapping(path = "/letter/list" ,method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
//        Integer.valueOf("abc");
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        // [2]页面上  设置  当前会话数量
        page.setRows(messageService.findConversationCount(user.getId()));

        // [1](分页)查询当前用户  的会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(),page.getLimit());

        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation",message);
                //[4]查询某个会话  所包含的 私信数量
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                //[5]查询  某个用户 的某个会话的 未读私信的数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));

                int targetId = (user.getId() == message.getFromId() ? message.getToId() : message.getFromId());
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        // 查询  未读 消息的数量 => [4]查询  某个用户 的某个会话的 未读私信的数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";
    }

    // 02. 私信详情
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // [3].（分页）查询当前会话 的私信列表
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList != null){
            for (Message message: letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters",letters);

        // 查询 私信目标
        model.addAttribute("target",getLetterTarget(conversationId));

        // 将私信列表中  未读的私信状态  改为已读的状态
        // 获取私信列表中  未读的私信id列表
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            // 设置已读
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    // 获取私信列表中  未读的私信id列表
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if(letterList != null){
            for (Message message: letterList){
                // 如果 我是 接受者的 身份
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus() ==0 ){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 =Integer.parseInt(ids[0]);
        int id1 =Integer.parseInt(ids[1]);

        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    // 新增 一条 消息
    @RequestMapping(path = "/letter/send" , method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
//        Integer.valueOf("abc");
        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());

        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else{
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

}