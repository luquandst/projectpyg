package com.luquan.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    /**
     * 查询当前定义的用户
     *
     * @return
     */
    @RequestMapping("getUser")
    public void getUserName() {
        //获取当前登录的用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //输出当前的用户名
        System.out.println("name = " + name);
    }
}
