package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/getValidCode")
    public Result getValidCode(String phone){
        System.out.println("正在向后台发送电话号码");
        System.out.println("phone = " + phone);
        try {
            userService.getValidCode(phone);
            return new Result(true,"获取短信验证码成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"获取短信验证码失败");
    }

    @RequestMapping(value = "/reg",method = RequestMethod.POST)
    public Result reg(@RequestBody TbUser user,String validCode){
        System.out.println("phone = " + user.getPhone());
        System.out.println("validCode = " + validCode);
        //判断用户输入的验证码是否正确
        boolean verify = userService.verify(user.getPhone(),validCode);
        if (verify){
            try {
                userService.addUser(user);
                return  new Result(true,"注册成功");
            } catch (Exception e) {
                e.printStackTrace();
                return  new Result(false,"注册失败");
            }
        }
        return  new Result(false,"验证码不正确");
    }
}
