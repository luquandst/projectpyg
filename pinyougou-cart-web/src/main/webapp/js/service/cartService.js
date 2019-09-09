app.service("cartService",function ($http) {

    //查看当前的购物车
    this.findCartList=()=>{
        return $http.get("./pay/findCartList.do?");
    }

    //往购物车中添加商品
    this.addCart=(itemId,num)=>{
        return $http.get("./pay/addToCart.do?itemId="+itemId+"&num="+num);
    }

    //根据当前登录的用户id查询该用户的所有的地址列表
    this.findAddressByUserId=()=>{
        return $http.get("./address/findAddressByUserId.do");
    }

    //提交订单
    this.submitOrder=(order)=>{
        return $http.post("./pay/addOrder.do",order);
    }
})