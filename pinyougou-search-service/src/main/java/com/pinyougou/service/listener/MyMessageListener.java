package com.pinyougou.service.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class MyMessageListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("正在导入到solr索引库....");
        try {
            //得到发送过来的消息
            TextMessage msg = (TextMessage) message;
            //得到消息的内容
            String text = msg.getText();
            //将消息的内容转化为集合
            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);
            System.out.println(tbItems);
            //遍历sku商品集合,将其中的规格转化为对象
            for (TbItem tbItem : tbItems) {
                //获取规格
                String spec = tbItem.getSpec();
                //将规格转化为对象
                Map map = JSON.parseObject(spec, Map.class);
                //设置规格选项
                tbItem.setSpecMap(map);
            }
            //将内容导入到索引库中
            itemSearchService.importList(tbItems);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
