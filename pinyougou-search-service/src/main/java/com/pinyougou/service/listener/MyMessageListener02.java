package com.pinyougou.service.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class MyMessageListener02 implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            //得到对方传来的消息
            ObjectMessage msg = (ObjectMessage) message;
            //根据消息得到商品的id集合
            Long [] ids = (Long[]) msg.getObject();
            //根据id删除商品信息
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
