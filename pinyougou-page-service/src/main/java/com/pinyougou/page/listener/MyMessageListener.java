package com.pinyougou.page.listener;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class MyMessageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("正在生成静态页面....");
            //得到传过来的信息对象
            ObjectMessage objectMessage = (ObjectMessage) message;
            System.out.println(objectMessage);
            //得到信息的内容,也就是商品的id的集合
            Long[] ids = (Long[]) objectMessage.getObject();
            //遍历商品id集合,生成html
            for (Long id : ids) {
                itemPageService.genItemHtml(id);
            }
            System.out.println("生成静态页面成功....");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
