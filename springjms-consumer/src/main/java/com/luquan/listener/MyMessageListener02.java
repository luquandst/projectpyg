package com.luquan.listener;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 定义监听器对象
 */
@Component
public class MyMessageListener02 implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            //得到对方发来的消息
            TextMessage textMessage = (TextMessage) message;
            //输出消息
            System.out.println(textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
