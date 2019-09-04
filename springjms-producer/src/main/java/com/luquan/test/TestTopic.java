package com.luquan.test;

import com.luquan.demo.TopicProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*xml")
public class TestTopic {
    @Autowired
    private TopicProducer topicProducer;

    //发送信息
    @Test
    public void sendMessage(){
        topicProducer.sendMessage();
    }
}
