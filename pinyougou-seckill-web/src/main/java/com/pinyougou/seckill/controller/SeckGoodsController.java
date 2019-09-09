package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seck")
public class SeckGoodsController {

    @Reference
    private SeckGoodsService seckGoodsService;

    /**
     * 查询当前所有的正在参与秒杀的商品
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeckillGoods> findAll(){
        return  seckGoodsService.findList();
    }

    /**
     * 根据商品的id查询秒杀商品的详情
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbSeckillGoods findOne(Long id){
        return seckGoodsService.findOne(id);
    }
}
