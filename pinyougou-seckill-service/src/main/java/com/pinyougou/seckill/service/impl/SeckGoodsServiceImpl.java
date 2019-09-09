package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.seckill.service.SeckGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

@Service
public class SeckGoodsServiceImpl implements SeckGoodsService {

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询当前的所有正在参与秒杀的商品列表
     * @return
     */
    @Override
    public List<TbSeckillGoods> findList() {
        //首先从redis缓存中查询所有的秒杀商品的订单
        System.out.println("正在从缓存中读取所有的秒杀订单");
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        //判断从redis中查询的结果，如果redis中没有，再从数据库中查找
        if (seckillGoods == null || seckillGoods.size() ==0){
            TbSeckillGoodsExample example = new TbSeckillGoodsExample();
            TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");            //商品的状态为审核状态
            criteria.andStockCountGreaterThan(0);      //商品库存要大于0
            criteria.andStartTimeLessThanOrEqualTo(new Date());//商品的发布日期要早于当前时间
            criteria.andEndTimeGreaterThan(new Date());        //商品的结束时间要晚于当前时间
            seckillGoods = tbSeckillGoodsMapper.selectByExample(example);
            //把查询到结果放入到缓存中去
            for (TbSeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(),seckillGood);
            }
        }
        return seckillGoods;
    }

    /**
     * 根据商品的id查询当前商品的详情
     * @param id
     * @return
     */
    @Override
    public TbSeckillGoods findOne(Long id) {
       return tbSeckillGoodsMapper.selectByPrimaryKey(id);
    }
}
