package com.pinyougou.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
public class MessageSender {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    //发送信息
    @GetMapping("/send")
    public void sendMsg(String msg){
        jmsMessagingTemplate.convertAndSend("msg1",msg);
    }
}
