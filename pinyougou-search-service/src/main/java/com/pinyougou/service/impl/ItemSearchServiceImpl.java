package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
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
        System.out.println("正在调用service层的方法...");
        //定义一个集合存放返回的结果
        Map<String,Object> map = new HashMap<>();
        //定义查询对象
        Query query = new SimpleQuery("*:*");
        //判断查询条件是否为空
        if(StringUtils.isNotBlank(searchMap.get("keywords")+"")){
            System.out.println("查询条件不为空....");
            query.addCriteria(new Criteria("item_keywords").is(searchMap.get("keywords")));
        }else{
            query.addCriteria(new Criteria("item_keywords"));
        }
        //指定分页参数
        query.setOffset(0);
        query.setRows(40);
        //根据查询关键字查询索引库
        System.out.println("分完页了.....");
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);

        List<TbItem> content = tbItems.getContent();
        //将查询结果放入到结合中去
        System.out.println("正在放入到mapr中....");
        map.put("rows",content);
        return map;
    }
}
