app.controller("cartController",function ($scope,$controller,cartService) {
    $controller('baseController',{$scope:$scope});//继承

    //查询当前的购物车列表
    $scope.findCartList=()=>{
        cartService.findCartList().success(response=>{
            $scope.cartList = response;
            $scope.total = count();
        })
    }

    //往购物车中添加商品
    $scope.addCart=(itemId,num)=>{
        cartService.addCart(itemId,num).success(response=>{
            if (response.success){
                //重新刷新商品列表
                $scope.findCartList();
            }else {
                alert("添加失败");
            }
        })
    }

    //统计商品数量以及商品的总金额
    count=()=>{
        var total = {"sum":0,"money":0};
        var cartList = $scope.cartList;
        //遍历集合进行统计
        for (var i=0;i<cartList.length;i++){
            //遍历订单详情集合
            var orderItems = cartList[i].orderItemList;
            for (var j=0;j<orderItems.length;j++){
                total.sum += orderItems[j].num;
                total.money += orderItems[j].totalFee;
            }
        }
        return total;
    }

})