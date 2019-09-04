package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.group.Goods;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;
import java.util.List;


/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
//	@Reference
//	private ItemSearchService itemSearchService;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Queue goodsDelete;
	@Autowired
	private Queue goodsUpdateStatus;
//	@Reference
//	private ItemPageService itemPageService;
	@Autowired
	private Topic genHtml;

	/**
	 * 生成静态页（测试）
	 * @param goodsId
	 */
	@RequestMapping("/genHtml")
	public void genHtml(final Long goodsId){
		System.out.println("正在调用生成html页面的方法.....");
//		itemPageService.genItemHtml(goodsId);
		jmsTemplate.send(genHtml, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(goodsId+"");
			}
		});
	}


	/**
	 * 审核商品的状态
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		try {
			goodsService.updateStatus(ids,status);
			System.out.println("商品状态为"+status);
			for (Long id : ids) {
				System.out.println(id.toString());
			}
			//如果商品的审核状态为1的话，就添加到索引库
			if (status.equals("1")){
				//查询出所有的商品
				System.out.println("正在查询所有的商品");
				final List<TbItem> list = goodsService.findItemListByGoodsIdandStatus(ids, status);
				System.out.println(list);
				//如果集合不为空的话，就将集合中的商品导入到索引库
				if (list.size()>0){
					System.out.println("正在导入索引库");
//					itemSearchService.importList(list);
					//审核通过的话就发送消息
					jmsTemplate.send(goodsUpdateStatus,new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							//将查询到商品列表转化成json字符串
							String jsonString = JSON.toJSONString(list);
							//创建要发送的信息
							TextMessage textMessage = session.createTextMessage(jsonString);
							return textMessage;
						}
					});
				}
			}
			return new Result(true,"审核成功...");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"审核失败...");
		}
	}
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//从索引库中删除
//			itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			//将要删除商品的数组发送给消息中间件
			jmsTemplate.send(goodsDelete, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody(required = false) TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
}
