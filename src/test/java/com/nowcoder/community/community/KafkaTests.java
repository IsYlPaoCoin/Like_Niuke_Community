package com.nowcoder.community.community;

import lombok.ToString;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ClassName: KafkaTests
 * Package: com.nowcoder.community.community
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/12 15:35
 * @Version 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() {
        kafkaProducer.sendMessage("test","PHDVB干嘛呢");
        kafkaProducer.sendMessage("test","呕吼");

        // 生产者发消息是  主动的 ，消费者收消息是  被动地
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Component
class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic,content);
    }
}

@Component
class KafkaConsumer {
    @KafkaListener(topics ={"test"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
        //PHDVB干嘛呢
        //呕吼
    }
}