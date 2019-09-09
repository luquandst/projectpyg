app.service("cartService",function ($http) {

    //查看当前的购物车
    this.findCartList=()=>{
        return $http.get("./pay/findCartList.do?");
    }

    //往购物车中添加商品
    this.addCart=(itemId,num)=>{
        return $http.get("./pay/addToCart.do?itemId="+itemId+"&num="+num);
    }
})