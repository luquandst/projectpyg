package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;

import java.util.List;

public interface SeckGoodsService {

    /**
     * 查询所有当前正在参加秒杀的商品
     * @return
     */
    List<TbSeckillGoods> findList();

    /**
     * 根据秒杀商品的id查询秒杀商品的详情
     * @param id
     * @return
     */
    TbSeckillGoods findOne(Long id);
}
