package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.group.Cart;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加商品到购物车，判断原来的购物车列表中是否具有该商家id的购物车
     * 有的话，还要判断该购物车中是否具有该件商品，有的话就修改数量和金额，没有的话就添加订单
     * 没有该购物车的话，就根据商家的id创建一个新购物车
     * 同时,需要注意商品的数量和购物车之间的关系
     * @param cartList 原来的购物车
     * @param itemId  商品id
     * @param num    商品数量
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //根据商品的id获取商品的信息
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        //根据商品信息获取商家的id
        String sellerId = tbItem.getSellerId();
        //查询是否具有购物车中是否有对应商家id的商家，有的话就返回该购物车，没有的话就返回空
        Cart cart = searchCartBySellerId(cartList,sellerId);
        if (cartList == null){
            cartList = new ArrayList<>();
        }
        if (cart == null){
            //如果该商家没有购物车的话，就创建一个购物车
            cart = createCart(tbItem,sellerId,num,itemId);
            //将购物车添加到购物车列表中
            cartList.add(cart);
        }else {
            //获取购物车订单列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //如果购物车列表中有该商家的购物车，就判断该购物车的订单列表中是否具有该件商品
            TbOrderItem orderItem = checkOrderListByItemId(cart,itemId);
            if (orderItem == null){
                //如果该商品的订单为空的话，就创建订单并添加到订单列表中
                orderItem = createOrderItem(tbItem,sellerId,num,itemId);
                //将订单添加到订单列表中
                orderItemList.add(orderItem);
            }else {
                //如果不为空的话，就修改商品的数量和小计
                orderItem.setNum(orderItem.getNum()+num);
                BigDecimal totalFee = new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum());
                orderItem.setTotalFee(totalFee);
                //更新订单列表中该商品订单的信息
                updateOrderItemList(orderItemList,orderItem);
                //处理特殊情况
                //当商品的数量小于0时候，就删除该订单
                if (orderItem.getNum() <= 0){
                    orderItemList.remove(orderItem);
                }
                //当购物车中订单列表数量为0的时候，将删除该购物车
                if (orderItemList.size() == 0){
                    cartList.remove(cart);
                }
            }
            //将订单的列表重新设置到购物车
            cart.setOrderItemList(orderItemList);
            //将购物车更新到购物车列表
            updateCartList(cartList,cart);
        }
        //返回购物车列表
        return cartList;
    }

    /**
     * 将购物车中的内容添加到的redis缓存中
     * 以用户名为key，购物车列表为value将数据存入到缓存中
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     * 将cookie中购物车和redis中的购物车进行整合
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    @Override
    public List<Cart> mergeCart(List<Cart> cookieCartList, List<Cart> redisCartList) {
        //遍历cookie中购物车的集合，将其添加到redis数据库中
        for (Cart cookieCart : cookieCartList) {
            for (TbOrderItem orderItem : cookieCart.getOrderItemList()) {
                //更新redis数据库的内容
                redisCartList = addGoodsToCartList(redisCartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisCartList;
    }

    /**
     * 根据用户名从redis中获取该用户的购物车列表
     * @param username
     * @return
     */
    @Override
    public List<Cart> getCartListFromRedis(String username) {
        //获取该用户的购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        //购物车为空
        if (cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 更新购物车中具有相同商家id的购物车
     * @param cartList
     * @param cart
     */
    private void updateCartList(List<Cart> cartList, Cart cart) {
        //遍历购物车的集合
        for(int i=0;i<cartList.size();i++){
            //删除原有的购物车，加入新购物车
            if (cartList.get(i).getSellerId().equals(cart.getSellerId())){
                cartList.remove(i);
                cartList.add(cart);
            }
        }

    }

    /**
     * 更新集合中某个对象:更新商品订单列表中某个id对应的商品信息
     * @param orderItemList
     * @param orderItem
     */
    private void updateOrderItemList(List<TbOrderItem> orderItemList, TbOrderItem orderItem) {
        //遍历集合中的所有元素
        for (int i = 0;i<orderItemList.size();i++){
            if (orderItemList.get(i).getItemId().equals(orderItem.getId())){
                //删除原来的订单并将新的订单添加进来
                orderItemList.remove(i);
                orderItemList.add(orderItem);
            }
        }
    }


    /**
     * 判断购物车中是否具有某个商品的订单,有的话就返回该商品订单
     * @param cart
     * @param itemId
     * @return
     */
    private TbOrderItem checkOrderListByItemId(Cart cart, Long itemId) {
        //遍历该购物车中的所有商品订单
        for (TbOrderItem orderItem : cart.getOrderItemList()) {
            //入某个订单含有该商品的id，就返回该订单
            if (orderItem.getItemId().equals(itemId)){
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建一个购物车，并将订单详情添加到购物车中
     * @param tbItem
     * @param sellerId
     * @param num
     * @param itemId
     * @return
     */
    private Cart createCart(TbItem tbItem, String sellerId,Integer num, Long itemId) {
        System.out.println(tbItem.toString());
        System.out.println("num = " + num);
        System.out.println("itemId = " + itemId);
        //创建一个购物车对象
        Cart cart = new Cart();
        //设置购物车商家id和商家名称
        cart.setSellerId(sellerId);
        cart.setSellerName(tbItem.getSeller());
        //根据商品id和商品信息创建一个订单详情
        TbOrderItem item = createOrderItem(tbItem,sellerId,num,itemId);
        System.out.println("item = " + item);
        //获取购物车的订单列表
        List<TbOrderItem> orderItemList = new ArrayList<>();
        //将订单添加到订单列表中
        orderItemList.add(item);
        //设置购物车
        cart.setOrderItemList(orderItemList);
        return cart;
    }

    /**
     * 创建一个订单详情
     * @param tbItem
     * @param sellerId
     * @param num
     * @param itemId
     * @return
     */
    private TbOrderItem createOrderItem(TbItem tbItem, String sellerId, Integer num, Long itemId) {
        //定义一个订单详情
        TbOrderItem orderItem = new TbOrderItem();
        //为订单的详情进行赋值
        orderItem.setItemId(itemId);
        orderItem.setSellerId(sellerId);
        orderItem.setGoodsId(tbItem.getGoodsId());
        orderItem.setNum(num);
        orderItem.setPrice(tbItem.getPrice());
        orderItem.setTitle(tbItem.getTitle());
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setTotalFee(new BigDecimal(num*tbItem.getPrice().longValue()));
        //返回该订单
        return orderItem;
    }


    /**
     * 判断购物车列表中是否有某个商家的购物车，商家由商家id判断
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        //遍历购物车的列表
        if (cartList != null){
            for (Cart cart : cartList) {
                //如果某个购物车的商家id与传入的商家id相等，就返回该购物车，否则就返回空
                if (cart.getSellerId().equals(sellerId)){
                    return cart;
                }
            }
        }
        return null;
    }
}
