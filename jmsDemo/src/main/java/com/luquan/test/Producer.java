package com.luquan.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;

/**
 * 测试点对点的消息中间件
 */

public class Producer {
    public static void main(String[] args) {
        try {
            //定义连接工厂
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
            //创建连接对象
            Connection connection = connectionFactory.createConnection();
            //开始连接
            connection.start();
            //开始建立会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //定义目标对象
            Queue destination = new ActiveMQQueue("test-queue");
            //创建生产者
            MessageProducer producer = session.createProducer(destination);
            //创建消息
            TextMessage message = session.createTextMessage("欢迎大家学习消息中间件");
            //发送消息
            producer.send(message);
            //关闭所有的资源
            producer.close();
            session.close();
            connection.close();


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
