package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
    private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
	    //添加就是删除原来的缓存
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
	    //得到原来商品对象的分类id
        Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
        //根据分类id删除缓存
        redisTemplate.boundHashOps("content").delete(categoryId);
        //如果修改了分类，就删除现在的缓存
        if (categoryId.longValue() !=  content.getCategoryId().longValue()){
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }

        contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
		    //删除数据先查出商品分类id，再根据分类id进行删除
            Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
            //删除缓存
            redisTemplate.boundHashOps("content").delete(categoryId);
            contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 根据广告的分类id查询广告
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<TbContent> findContentByCateId(String categoryId) {
	    //从缓存中读取数据，如果缓存不存在的话，就将当前的数据存入到缓存中
        List<TbContent> content = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
        //如果缓存存在的话，就输出提示信息,并返回缓存中的数据
        if ( content != null){
            System.out.print("正在从缓存中读取数据");
            return content;
        }
        //缓存不存在的话，就从数据库中查询数据，并插入到缓存中
        //创建一个广告实例
		TbContentExample example = new TbContentExample();
		//创建查询条件
        Criteria criteria = example.createCriteria();
        //添加查询条件
        criteria.andCategoryIdEqualTo(new Long(categoryId));
        //返回广告的集合
		 content = contentMapper.selectByExample(example);
		 //将查询到的数据存如到缓存中
        redisTemplate.boundHashOps("content").put(categoryId,content);
		return  content;
	}

}
