package com.luquan.test;

import com.luquan.demo.QueneProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*.xml")
public class TesQuene {

    @Autowired
    private QueneProducer queneProducer;

    //测试发送消息
    @Test
    public void sendMsg(){
        queneProducer.sendMessage();
    }
}
