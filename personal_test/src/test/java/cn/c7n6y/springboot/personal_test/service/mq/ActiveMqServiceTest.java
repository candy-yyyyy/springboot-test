package cn.c7n6y.springboot.personal_test.service.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.Queue;
import javax.jms.Topic;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActiveMqServiceTest {
    @Autowired
    private Queue queue;

    @Autowired
    private Topic topic;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    // queue模式
    @Test
    public void send() throws Exception {
        //方法一：添加消息到消息队列
        jmsMessagingTemplate.convertAndSend(queue, "test123");
        //方法二：这种方式不需要手动创建queue，系统会自行创建名为test的队列
        //jmsMessagingTemplate.convertAndSend("test", name);文出处链接及本声明。
    }

    // 使用JmsListener配置消费者监听的队列，其中name是接收到的消息
    @JmsListener(destination = "ActiveMQQueue")
    // SendTo 会将此方法返回的数据, 写入到 OutQueue 中去.
    @SendTo("SQueue")
    @Test
    public void get() throws Exception {
        System.out.println("成功接收数据：");
    }

    // topic模式
    @Test
    public void sendTopic() throws Exception {
//        jmsMessagingTemplate.convertAndSend(topic, "test321");
        jmsTemplate.setDeliveryMode(2);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.convertAndSend(topic, "test321");
    }

    // 使用JmsListener配置消费者监听的队列，其中name是接收到的消息
//    @JmsListener(destination = "ActiveMQQueue")
    // SendTo 会将此方法返回的数据, 写入到 OutQueue 中去.
    @SendTo("SQueue")
    @Test
    public void getTopic() throws Exception {
        System.out.println("成功接收数据：");
    }
}