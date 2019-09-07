package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinoyougou.util.CookieUtil;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.group.Cart;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private OrderService orderService;

    //查看当前cookie中所有购物车集合
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //判断当前用户是否登录，登录了就从redis中获取数据，没有登录的话就从cookie中获取数据
        //判断用户登录的条件就是用户名书否等于匿名用户
        //获取当前登录的用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("name = " + name);
        //从cookie获取购物车列表
        String cartString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        System.out.println("cartString = " + cartString);
        //如果购物车列表为空
        if (StringUtils.isEmpty(cartString)){
            cartString = "[]";
        }
        //将得到的结果转化为list集合
         List<Cart> cartList =JSON.parseArray(cartString,Cart.class);
        //判断当前用户是否登陆
        if ("anonymousUser".equals(name)){
            //如果当前用户是匿名用户的话，就返回cookie中的数据
            System.out.println("cartList = " + cartList);
            return cartList;
        }else {
            //如果当前用户不是匿名用户的话，就从redis中获取查找当前用户的购物车
            List<Cart> cartListFromRedis = cartService.getCartListFromRedis(name);
            System.out.println("cartListFromRedis = " + cartListFromRedis);
            if (cartList.size()>0){
                //将cookie中的数据和redis中的数据进行整合
                cartListFromRedis = cartService.mergeCart(cartList, cartListFromRedis);
                //清空cookies中的内容
                CookieUtil.deleteCookie(request,response,"cartList");
                //将购物车列表保存到redis中
                cartService.saveCartListToRedis(name,cartListFromRedis);
            }
            return cartListFromRedis;
        }
    }

    //添加商品到购物车
    @RequestMapping("/addToCart")
    //解决跨域请求的注解(spring4.2版本之后才支持)
    @CrossOrigin(origins = "http://localhost:9104",allowCredentials = "true")
    public Result addTbItemToCart(Long itemId,int num){
        //设置跨域请求的解决方案
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9104");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        try {
            //获取当前cookie中购物车
//            List<Cart> cartList = findCartList();
            String cartString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            //转换格式
            List<Cart> cartList = JSON.parseArray(cartString, Cart.class);
            System.out.println("cartList = " + cartList);
            //如果购物车列表为空
            if (StringUtils.isEmpty(cartString)){
                cartString = "[]";
            }
            //进行添加商品
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            //将添加后购物车重新放入到cookie当中
            CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"UTF-8");
            return new Result(true,"添加到购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加到购物车失败");
        }
    }

    /**
     * 添加订单：添加订单到订单表，同时添加订单详情到订单详情表
     * @param tbOrder
     * @return
     */
    @RequestMapping("/addOrder")
    public Result addOrder(@RequestBody TbOrder tbOrder){
        System.out.println("tbOrder = " + tbOrder.toString());
        try {
            //获取当前登录的用户id
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            //给tbOrder添加属性
            tbOrder.setUserId(userId);
            tbOrder.setSourceType("2");
            //保存到数据库
            orderService.add(tbOrder);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
}
