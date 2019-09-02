package com.luquan.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;

/**
 * 测试发布/订阅消息中间件
 */
public class TopicConsumer {
    public static void main(String[] args) {
        try {
            //创建连接工厂
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
            //定义连接对象
            Connection connection = connectionFactory.createConnection();
            //开始连接
            connection.start();
            //通过连接对象创建会话对象
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建目标对象
            Topic destination = new ActiveMQTopic("test-topic");
            //创建消费者对象
            MessageConsumer consumer = session.createConsumer(destination);
            //创建监听器对象
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        //获取信息
                        TextMessage textMessage = (TextMessage) message;
                        //输出信息内容
                        System.out.println(textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            //等待系统输入
            System.in.read();
            //关闭所有的资源
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
