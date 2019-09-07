package com.pinyougou.user;

import com.pinyougou.pojo.TbAddress;

import java.util.List;

public interface AddressService {

    /**
     * 根据用户的id获取当前用户的地址列表
     * @param userId
     * @return
     */
    List<TbAddress> findAddressByUserId(String userId);
}
