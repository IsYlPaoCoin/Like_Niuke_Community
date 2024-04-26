package com.nowcoder.community.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * ClassName: EventProducer
 * Package: com.nowcoder.community.community.event
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/14 10:04
 * @Version 1.0
 */

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 处理 事件
    public void fireEvent(Event event){
        // 将事件 发送到指定的 主题（生产者）
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));

    }

}
