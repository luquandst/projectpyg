package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private TbUserMapper userMapper;

    //通过手机号获取验证码
    @Override
    public void getValidCode(final String phone) {
        System.out.println("正在调用service层获取验证码");
        //1.生成验证码
        final String code =  (long)(Math.random()*1000000) + "";
        System.out.println("code = " + code);
        //清除原来本机的缓存
//        redisTemplate.boundHashOps("validCode").delete(phone);
        //2.将手机号和验证码存入到缓存中
        redisTemplate.boundHashOps("validCode").put(phone,code);
        //将生成的验证码和手机号发送给springboot微服务项目
        jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //定义信息对象
                MapMessage mapMessage = session.createMapMessage();
                //将手机号和验证码放入到信息对象中去
                mapMessage.setString("phone",phone);
                //将验证码放入到一个集合中去然后再转换格式放入到信息中
                Map map = new HashMap<>();
                map.put("code",code);
                String jsonString = JSON.toJSONString(map);
                mapMessage.setString("param",jsonString);
                //发送给springboot的微服务项目
                return mapMessage;
            }
        });

    }

    @Override
    public boolean verify(String phone,String validCode) {
        System.out.println("phone = " + phone);
        System.out.println("validCode = " + validCode);
        //从redis读取用户生成的验证码
        String code = (String) redisTemplate.boundHashOps("validCode").get(phone);
        //比较两次输入的验证码是否一致,并返回结果
        return code.equals(validCode);
    }

    /**
     * 往数据库添加用户
     * @param user
     */
    @Override
    public void addUser(TbUser user) {
        userMapper.insert(user);
    }


}
