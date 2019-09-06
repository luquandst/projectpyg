app.service("cartService",function ($http) {

    //查看当前的购物车
    this.findCartList=()=>{
        return $http.get("./cart/findCartList.do?");
    }

    //往购物车中添加商品
    this.addCart=(itemId,num)=>{
        return $http.get("./cart/addToCart.do?itemId="+itemId+"&num="+num);
    }
})