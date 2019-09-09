package com.pinyougou.pay.service;

import com.pinyougou.group.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
     List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );

    /**
     * 把购物车中的内容添到缓存中去
     * @param username
     * @param cartList
     */
    void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 把cookie中内容和redis中的内容进行整合
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    List<Cart> mergeCart(List<Cart> cookieCartList,List<Cart> redisCartList);

    /**
     * 根据当前登录的用户名从redis中获取该用户的购物车
     * @param username
     * @return
     */
    List<Cart> getCartListFromRedis(String username);

}
