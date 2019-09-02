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
public class QueneProducer {
    @Autowired
    private JmsTemplate jmsTemplate;

    //定义发送消息的方法
    public void sendMessage(){
        jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //定义发送的消息
                TextMessage textMessage = session.createTextMessage("整合spring框架正在发送点对点的消息");
                return textMessage;
            }
        });
    }
}
