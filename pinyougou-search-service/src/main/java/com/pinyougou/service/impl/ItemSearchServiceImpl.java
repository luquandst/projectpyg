package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 查询索引库的内容，并以map的格式返回到前端
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> searchItems(Map searchMap) {
        //定义一个集合存放返回的结果
        Map<String,Object> map = new HashMap<>();
        //定义查询对象
        Query query = new SimpleQuery("*:*");
        //定义查询条件
        Criteria criteria = null;
        //判断查询条件是否为空
        if (searchMap == null){
           criteria = new Criteria("item_keywords");
        }else {
            criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        }
        //根据查询关键字查询索引库
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        //将查询结果放入到结合中去
        map.put("rows",tbItems);
        return map;
    }
}
