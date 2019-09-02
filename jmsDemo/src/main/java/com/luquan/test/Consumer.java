package com.luquan.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;

public class Consumer {
    public static void main(String[] args) {
        try {
            //创建连接工厂
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
            //创建连接
            Connection connection = connectionFactory.createConnection();
            //开始连接
            connection.start();
            //根据连接获取会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建目标对象
            Queue destination = new ActiveMQQueue("test-queue");
            //创建消费者对象
            MessageConsumer consumer = session.createConsumer(destination);
            //创建监听器对象监听消息
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        //获取信息
                        TextMessage textMessage = (TextMessage) message;
                        //打印出信息
                        System.out.println(textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            //等待键盘输入
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
