package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbSpecificationMapper tbSpecificationMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询索引库的内容，并以map的格式返回到前端
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> searchItems(Map searchMap) {
        //定义一个集合存放返回的结果
        Map<String, Object> map = new HashMap<>();

        //调用高亮查询的方法
        Map<String, Object> stringObjectMap = hilightQuery(searchMap);
        //将高亮查询结果放入到结合中
        map.putAll(stringObjectMap);

        //进行分组查询
        List<String> categoryList = searchByCategory(searchMap);
        String category = "";
        if(StringUtils.isNotBlank(searchMap.get("category")+"")){
            category = searchMap.get("category")+"";
        }else{ // 如果没有值，就取分类的第一个值作为分类的值，得到模板id，根据模板id得到品牌及规格列表
            category = categoryList.get(0);
        }
        //将分组查询结果添加到map集合中去
        map.put("categoryList",categoryList);
        System.out.println(categoryList);

        //进行分类查询,默认页面上加载的是第一条数据
        Map catoryMap = searchByCatory(category);
        map.putAll(catoryMap);

        return map;

    }

    //进行分类查询
    public Map searchByCatory(String category){
        //定义一个集合存放结果
        Map map = new HashMap();
        //根据当前的分类名称获取模板id
        Long typeid = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //根据当前的模板id分别从redis中取出品牌和规格的列表
        List<TbBrand> brandList = (List<TbBrand>) redisTemplate.boundHashOps("brandList").get(typeid);
        List<TbSpecification> specList = (List<TbSpecification>) redisTemplate.boundHashOps("specList").get(typeid);
        //把查询到的结果集放入到map中去
        map.put("brandList",brandList);
        map.put("specList",specList);

        //返回集合
        return map;
    }


    //进行分组查询
    private List<String> searchByCategory(Map searchMap) {
        //定义一个集合存放分组查询的对象
        List<String> categoryList = new ArrayList<>();
        //定义分组查询的对象
        Query query = new SimpleQuery();
        //添加查询条件
        if (StringUtils.isNotEmpty(searchMap.get("keywords")+"")) {
            query.addCriteria(new Criteria("item_keywords").is(searchMap.get("keywords")));
        }else {
            query.addCriteria(new Criteria("item_keywords"));
        }
        //定义分组查询选项
        GroupOptions groupOptions = new GroupOptions();
        //设置分组的条件
        groupOptions.addGroupByField("item_category");
        //把分组查询和查询对象绑定在一起
        query.setGroupOptions(groupOptions);

        //进行分组查询,得到分组查询的页面
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        //得到分组查询的结果
        GroupResult<TbItem> category = tbItems.getGroupResult("item_category");
        //得到分组查询的入口
        Page<GroupEntry<TbItem>> groupEntries = category.getGroupEntries();
        //得到分组查询的具体内容
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //遍历分组查询的内容，将其放入到的目标结合中去
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            //得到具体的分组的对象的值
            String groupValue = tbItemGroupEntry.getGroupValue();
            //将对象的值存放到集合中去
            categoryList.add(groupValue);
        }

        return categoryList;
    }

    //定义高亮查询的方法
    public Map<String, Object> hilightQuery(Map searchMap) {
        //定义一个集合存放返回的结果
        Map<String, Object> map = new HashMap<>();
        //定义高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //添加查询条件
        if (StringUtils.isNotEmpty(searchMap.get("keywords") + "")) {
            query.addCriteria(new Criteria("item_keywords").is(searchMap.get("keywords")));
        } else {
            query.addCriteria(new Criteria("item_keywords"));
        }
        //设置分页
        query.setOffset(0);
        query.setRows(16);

        //设置过滤查询
        //定义过滤查询条件
        FilterQuery filterQuery = new SimpleFilterQuery();
        //添加品牌的过滤条件
        if (StringUtils.isNotEmpty(searchMap.get("brand")+"")){
            //创建查询条件
            Criteria criteria = new Criteria("item_brand").is(searchMap.get("brand"));
            //把查询条件添加到过滤查询中
            filterQuery.addCriteria(criteria);
        }

        //添加分类过滤条件
        if (StringUtils.isNotEmpty(searchMap.get("category")+"")){
            //创建查询条件
            Criteria criteria = new Criteria("item_category").is(searchMap.get("category"));
            //把查询条件添加到过滤查询中
            filterQuery.addCriteria(criteria);
        }

        //添加规格过滤条件
        if (StringUtils.isNotEmpty(searchMap.get("spec")+"")){
            //获取所有的规格
            Map spec = JSON.parseObject(searchMap.get("spec").toString(), Map.class);
            //创建过滤条件
            for (String s : map.keySet()) {
                Criteria criteria = new Criteria("item_spec_" + s).is(spec.get(s));
                //把查询条件添加到过滤查询中
                filterQuery.addCriteria(criteria);
            }

        }

        //把过滤查询和高亮查询绑定在一块
        query.addFilterQuery(filterQuery);


        //定义高亮查询选项
        HighlightOptions options = new HighlightOptions();
        //设置高亮查询的数据
        options.addField("item_title");
        //为高亮查询设置前缀和后缀
        options.setSimplePrefix("<span style='color:red'>");
        options.setSimplePostfix("</span>");
        //把高亮查询选项和高亮查询绑定在一块
        query.setHighlightOptions(options);

        //进行高亮查询，得到高亮查询页面
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //得到高亮查询的入口
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        //遍历高亮查询的入口
        for (HighlightEntry<TbItem> entry : highlighted) {
            //得到页面上没有经过高亮查询的实体
            TbItem entity = entry.getEntity();
            //得到高亮查询数据
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();
            //得到第一个高亮查询的字段
            if (highlights != null && highlights.size() > 0) {
                HighlightEntry.Highlight highlight = highlights.get(0);
                //获取高亮查询的片段
                List<String> snipplets = highlight.getSnipplets();
                //如果该片段是多域的，就设置多域的第一个值为高亮
                if (snipplets != null && snipplets.size() > 0) {
                    entity.setTitle(snipplets.get(0));
                }
            }
        }
        //将得到的高亮数据存放到集合中去
        map.put("rows", tbItems.getContent());
        //返回高亮数据
        return map;
    }


}
