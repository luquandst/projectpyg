package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class LoginController {

    //获取当前登录的用户名
    @RequestMapping("/login")
    public Map<String,String> getUserName(){
        //获取当前登录的用户的用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //定义一个Map集合
        Map<String,String>  map = new HashMap<>();
        //将获取用户名放入到集合中
        map.put("name",username);
        //返回用户名
        return map;
    }
}
