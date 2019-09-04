package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    //获取当前的用户名称
    @RequestMapping("/getUserName")
    public Map getUserName(){
        //定义一个map集合
        Map map = new HashMap();
        //获取当前的用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //把用户名放入到集合中去
        map.put("name",name);
        return map;
    }
}
