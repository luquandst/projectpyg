package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名字查询到用户对象
        TbSeller tbSeller = sellerService.selectByUserName(username);
        //判断用户是否为空
        if (tbSeller!=null){
            //如果用户审核通过，就允许登录
            if (tbSeller.getStatus().equals("1")){
                //为用户指定角色列表
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                //允许用户通过
                return new User(username,tbSeller.getPassword(),authorities);
            }
            return null;
        }
        return null;
    }
}
