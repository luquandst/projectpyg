package com.pinyougou.user;

import com.pinyougou.pojo.TbUser;

public interface UserService {
    //获取短信验证码
    void getValidCode(String phone);

    //比较用户输入的验证码和生成的验证码是否一致
    boolean verify(String phone,String validCode);

    //用户注册
    void addUser(TbUser user);
}
