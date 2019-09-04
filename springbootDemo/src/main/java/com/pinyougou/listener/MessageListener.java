package com.pinyougou.listener;

import com.aliyuncs.exceptions.ClientException;
import com.pinyougou.utils.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class MessageListener {

    @Autowired
    private SmsUtil smsUtil;

    //接受信息
    @JmsListener(destination = "msg1")
    public void receiveMsg(String msg){
        System.out.println("msg = " + msg);
    }

    //监听userService发送过来的手机号和验证码
    @JmsListener(destination = "code")
    public void sendMessage(Map map) throws IOException {
        try {
            System.out.println("调用springboot获取信息");
            //获取手机号
            String phone = (String) map.get("phone");
            //获取验证码参数
            String param = (String) map.get("param");
            System.out.println("phone = " + phone);
            System.out.println("param = " + param);
            //调用工具类的方法发送短信
            smsUtil.sendSms(phone, param);
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }
}
