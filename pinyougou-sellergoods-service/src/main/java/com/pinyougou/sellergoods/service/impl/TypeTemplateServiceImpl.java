package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper tbSpecificationOptionMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        //查询到所有模板
        List<TbTypeTemplate> templates = findAll();
        //遍历模板的集合
        for (TbTypeTemplate template : templates) {
            //获取当前具体模板对应的品牌信息,并转化为对象
            String brandIds = template.getBrandIds();
            List<Map> brandList =  JSON.parseArray(brandIds, Map.class);
            //将模板id作为key，商品集合为value存放到缓存中
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);

            //获取所有的规格的信息,并转化为java对象
            List<Map> specList = findByTypeId(template.getId() + "");
            //以模板id为key，规格列表为值存放到redis数据库中
            redisTemplate.boundHashOps("specList").put(template.getId(),specList);
        }
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据模板id查询所有规格和规格下所有规格选项，并以List<map>的方式返回
     *
     * @param typeId
     * @return
     */
    @Override
    public List<Map> findByTypeId(String typeId) {
        //创建一个集合存放所有规格
        List<Map> list = new ArrayList<>();
        //根据模板id查询出当前模板
        TbTypeTemplate tbTypeTemplate = findOne(Long.parseLong(typeId));
        //得到所有的规格
        String specIds = tbTypeTemplate.getSpecIds();
        //将规格从字符串转化成对象
        list = JSON.parseArray(specIds, Map.class);
        //遍历规格的集合
        for (Map map : list) {
            //创建一个查询实例
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            //创建查询条件
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            //添加查询条件
            criteria.andSpecIdEqualTo(Long.parseLong(map.get("id").toString()));
            //根据该规格id查询出所有的规格选项
            List<TbSpecificationOption> options = tbSpecificationOptionMapper.selectByExample(example);
            //将查询到规格选项列表添加到规格中去
            map.put("options", options);
        }
        return list;
    }

}
