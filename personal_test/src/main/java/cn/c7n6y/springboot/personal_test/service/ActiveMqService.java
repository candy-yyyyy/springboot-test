package cn.c7n6y.springboot.personal_test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Queue;
import javax.jms.Topic;

@Service
public class ActiveMqService {

    @Autowired
    private Queue queue;

    @Autowired
    private Topic topic;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(String data) throws Exception {
        //方法一：添加消息到消息队列
//        jmsMessagingTemplate.convertAndSend(queue, data);
        jmsTemplate.convertAndSend(queue, data);
        //方法二：这种方式不需要手动创建queue，系统会自行创建名为test的队列
        //jmsMessagingTemplate.convertAndSend("test", name);文出处链接及本声明。
    }


    // 使用JmsListener配置消费者监听的队列，其中name是接收到的消息
    @JmsListener(destination = "ActiveMQQueue", containerFactory =
            "queueListenerContainerFactory")
    // SendTo 会将此方法返回的数据, 写入到 SQueue 中去.
//    @SendTo("SQueue") 如果使用此注解 需要return
    public void get(String data) throws Exception {
        System.out.println("成功接收queue数据：" + data);
    }

    // topic模式
    public void sendTopic(String data) throws Exception {
//        jmsMessagingTemplate.convertAndSend(topic, data);
        // jmsMessagingTemplate 是对 jmsTemplate的封装 如果需要使用到消息持久化 需要用jmsTemplate
        // 设置是否持久化要发送的消息：1-非持久化；2-持久化
        jmsTemplate.setDeliveryMode(2);
        jmsTemplate.setExplicitQosEnabled(true);
        // 设置是否持久化要发送的消息，true-持久化；false-非持久化
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.convertAndSend(topic, data);
    }

    // 使用JmsListener配置消费者监听的队列，其中name是接收到的消息
    @JmsListener(destination = "ActiveMQTopic", containerFactory =
            "topicListenerContainerFactory")
//    @SendTo("SQueue") 如果使用此注解 需要return
    public void getTopic(String data) throws Exception {
        System.out.println("成功接收topic数据：" + data);
    }
}
