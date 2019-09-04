package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemPageServiceImpl implements ItemPageService {
    //1.定义最终模板存放的位置
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    //2.根据商品id生成静态页面
    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            //2.1)得到配置对象
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //2.2)根据配置对象得到模板对象
            Template template = configuration.getTemplate("item.ftl");
            //2.3)根据商品id得到商品信息
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //2.4)将上面的数据放到map中
            Map data = new HashMap();
            //2.4.1)放基本的商品及描述信息
            data.put("goods",goods);
            data.put("goodsDesc",goodsDesc);
            //2.4.2)放分类信息一map中
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            data.put("itemCat1",itemCat1);
            data.put("itemCat2",itemCat2);
            data.put("itemCat3",itemCat3);

            //2.4.3)得到sku商品信息
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");                 //代表有效的商品
            example.setOrderByClause("is_default desc");          //按照默认值进行倒序排序
            List<TbItem> tbItems = itemMapper.selectByExample(example);
            //3.4.4)将sku商品列表放到map中
            data.put("itemList",tbItems);

            //2.5)构造输出流，向指定目录输出目标文件
            FileWriter writer = new FileWriter(pagedir + goodsId + ".html");
            //2.6)将结果页面输出
            template.process(data,writer);
            //2.7)关闭流
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}

