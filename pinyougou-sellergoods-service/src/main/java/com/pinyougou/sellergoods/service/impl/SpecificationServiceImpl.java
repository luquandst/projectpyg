package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Specification;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<Specification> findAll() {
		//创建一个存储Specification对象的集合
		List<Specification> list = new ArrayList<>();
		//查询所有的规格
		List<TbSpecification> tbSpecifications = specificationMapper.selectByExample(null);
		//遍历规格的集合
		for (TbSpecification tbSpecification : tbSpecifications) {
			//定义一个Specification对象
            Specification specification = new Specification();
            //根据tbSpecification的id查询对应的规格选项的集合
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(tbSpecification.getId());
            List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
            //给Specification这个对象进行赋值
            specification.setSpec(tbSpecification);
            specification.setSpecificationOptionList(tbSpecificationOptions);
            //把Specification添加到集合中去
            list.add(specification);
        }
		return list;
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
	    //往规格数据库插入一条规格
		specificationMapper.insert(specification.getSpec());
		//遍历规格选项的集合
        for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
            //设置规格选项的spec_id
            tbSpecificationOption.setSpecId(specification.getSpec().getId());
            //往规格选项中添加规格选项
            tbSpecificationOptionMapper.insert(tbSpecificationOption);
        }
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
        //1.修改规格表
        specificationMapper.updateByPrimaryKey(specification.getSpec());

        //2.先在tb_specification这个表中根据外键删除对应的记录
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specification.getSpec().getId());
        //3.根据外键id来删除规格选项
        tbSpecificationOptionMapper.deleteByExample(example);

        //4.在tb_specification这个表中添加对应的记录
        for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
            option.setSpecId(specification.getSpec().getId());
            tbSpecificationOptionMapper.insert(option);
        }

	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
	    //定义一个Specification对象
        Specification specification = new Specification();
        //根据di查询tbSpecification对象
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //根据id查询SpecificationOptionList集合
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
        //将tbSpecification和tbSpecificationOptions绑定到Specification
        specification.setSpec(tbSpecification);
        specification.setSpecificationOptionList(tbSpecificationOptions);
        return  specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	//查询所有
	@Override
	public List<Map> findAllByList() {
		return specificationMapper.findSpecList();
	}

}
