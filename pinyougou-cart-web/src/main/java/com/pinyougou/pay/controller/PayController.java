package com.pinyougou.pay.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinoyougou.util.IdWorker;
import com.pinyougou.pay.service.WxpayService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WxpayService wxpayService;

    /**
     * 生成支付的二维码，商品订单和总金额都设置成固定值
     * @return
     */
    @RequestMapping("/createPayCode")
    public Map<String,String> createPayCode(){
        IdWorker idWorker = new IdWorker();
        return wxpayService.createNative(idWorker.nextId()+"","1");
    }
}
