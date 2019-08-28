package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//往商品列表中添加商品
		goodsMapper.insert(goods.getGoods());

		//设置商品描述goodid为商品的id
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		//往商品描述的列表中添加商品描述
		goodsDescMapper.insert(goods.getGoodsDesc());

		//向tb_item表中添加数据
		//遍历所有的items
		insertItems(goods);
	}

	private void insertItems(Goods goods) {
		for (TbItem tbItem : goods.getItems()) {
			//设置标题
			tbItem.setTitle(goods.getGoods().getGoodsName());
			//设置品牌的名称
			tbItem.setBrand(brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId()).getName());
			//设置三级分类
			tbItem.setCategoryid(goods.getGoods().getCategory3Id());
			//根据三级分类查询三级名称
			Long category3Id = goods.getGoods().getCategory3Id();
			String categoryName = itemCatMapper.selectByPrimaryKey(category3Id).getName();
			tbItem.setCategory(categoryName);
			tbItem.setSeller(goods.getGoods().getSellerId());
			tbItem.setGoodsId(goods.getGoods().getId());
			tbItem.setUpdateTime(new Date());
			//设置图片
			String itemImages = goods.getGoodsDesc().getItemImages();
			//转换图片的格式
			List<Map> imageMap = JSON.parseArray(itemImages,Map.class);
			if(imageMap != null && imageMap.size() > 0){
				String url = (String) imageMap.get(0).get("url");
				tbItem.setImage(url);

			}
			tbItem.setCreateTime(new Date());
			//保存sku商品
			itemMapper.insert(tbItem);


		}
	}


	/**
	 * 修改商品信息
	 */
	@Override
	public void update(Goods goods){
		//修改商品信息
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//修改商品描述
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

		//修改sku商品信息过程就是先删除后增加,删除就是在sku列表中删除该外键值的选项
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//添加sku商品信息
		insertItems(goods);

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		//新建一个goods对象
		Goods goods = new Goods();
		//根据商品id查询到商品对象
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		//把商品对象设置到组合对象中
		goods.setGoods(tbGoods);

		//根据商品id查询到该商品的商品描述
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		//把商品描述设置到组合对象中去
		goods.setGoodsDesc(tbGoodsDesc);

		//查询到sku商品，由于是外键，需要创建一查询对象
		TbItemExample example = new TbItemExample();
		//定义查询条件
		TbItemExample.Criteria criteria = example.createCriteria();
		//添加查询条件
		criteria.andGoodsIdEqualTo(id);
		//查询所有sku商品
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		//将sku商品列表设置到组合类中
		goods.setItems(tbItems);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(String[] ids) {
		for(String id:ids){
			//根据id查询出商品
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(Long.parseLong(id));
			//删除是将商品isdelete属性修改为1
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 产品的审核
	 * @param ids
	 * @param status
	 */
	@Override
	public void updateStatus(String[] ids, String status) {
		//遍历ids数据，查询到所有good，并将该商品的状态修改为对应的状态
		for (String id : ids) {
			//查询到对应的商品
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(new Long(id));
			//设置tbGood的状态为相应的状态
			tbGoods.setAuditStatus(status);
			//更新商品
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

}
