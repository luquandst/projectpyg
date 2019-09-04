package com.pinyougou.sellergoods.service;

import com.pinyougou.group.Goods;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;


import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 根据商品的id和状态查询商品的信息
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status );

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();

	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	//产品的审核
	void updateStatus(Long[] ids, String status);

}
