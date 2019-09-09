package com.pinyougou.pay.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    @RequestMapping("/findAddressByUserId")
    public List<TbAddress> findAddressByUserId(){
        //获取当前登录用户的用户id
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //获取所有的地址列表
        return addressService.findAddressByUserId(userId);
    }
}
