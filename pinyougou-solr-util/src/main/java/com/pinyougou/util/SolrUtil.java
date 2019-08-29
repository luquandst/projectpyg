package com.pinyougou.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper tbItemMapper;

    //查询数据库的数据，并添加到索引库
    public void importItemsToSolr(){
        //查询出数据库的所有的sku商品信息，且商品的为审核状态
        //即商品的status属性的为1
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);

        //遍历商品的集合，将商品添加到索引库
        for (TbItem tbItem : tbItems) {
            //为sku商品的动态域赋值
            Map<String,String> spec = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(spec);
        }
        //将sku商品添加到solr索引库
        solrTemplate.saveBeans(tbItems);
        //提交
        solrTemplate.commit();


    }

    //查询所有的记录
    public void getAllItem(){
        Query query = new SimpleQuery("*:*");
        query.setOffset(0);         //从指定的位置开始查询
        query.setRows(10);          //每次查询10条
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = tbItems.getContent();
        System.out.println(content);
    }
}
