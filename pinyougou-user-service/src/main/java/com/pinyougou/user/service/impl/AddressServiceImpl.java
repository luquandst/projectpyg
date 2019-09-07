package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAddressExample;
import com.pinyougou.user.AddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private TbAddressMapper tbAddressMapper;

    /**
     * 根据当前用户的id获取用户地址列表
     * @param userId
     * @return
     */
    @Override
    public List<TbAddress> findAddressByUserId(String userId) {
        TbAddressExample example = new TbAddressExample();
        TbAddressExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        //返回当前用户的地址列表
        return tbAddressMapper.selectByExample(example);
    }
}
