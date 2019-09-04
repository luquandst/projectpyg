package com.luquan.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Component
public class TopicProducer {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(){
        jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //创建目标信息
                TextMessage textMessage = session.createTextMessage("spring与activemq整合发布/订阅消息");
                return textMessage;
            }
        });
    }

}
