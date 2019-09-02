package com.luquan.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;

/**
 * 测试发布/订阅消息中间件
 */
public class TopicProducer {
    public static void main(String[] args) {
        try {
            //创建连接的工厂
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
            //创建连接
            Connection connection = connectionFactory.createConnection();
            //开始连接
            connection.start();
            //根据连接获取会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建目标对象
            Topic destination = new ActiveMQTopic("test-topic");
            //创建生产者对象
            MessageProducer producer = session.createProducer(destination);
            //创建消息对象
            TextMessage textMessage = session.createTextMessage("你好，欢迎学习生产/订阅消息中间件");
            //发送消息
            producer.send(textMessage);
            //关闭所有的资源
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
