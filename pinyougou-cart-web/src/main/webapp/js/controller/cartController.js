app.controller("cartController",function ($scope,$controller,$location,cartService) {
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

    //根据当前登录的id查询对应用户的所有地址
    $scope.findAddressByUserId=()=>{
        alert("正在查询所有的地址....");
        cartService.findAddressByUserId().success(response=>{
            $scope.addressList = response;
            //遍历地址
            for (var i=0,len = response.length;i<len;i++ ){
                $scope.addr = response[i];
            }
        })
    }

    //点击的时候把选中的地址修改为当前的对应联系人的地址
    $scope.selectAddress=(address)=>{
        $scope.address = address;
    }

    //判断某个选项是否选中
    $scope.isSelected=(address)=>{
        return address == $scope.address;
    }

    //订单对象
    $scope.order={"paymentType":1,"receiverAreaName":"","receiver":"","receiverMobile":""};
    //选择付款方式
    $scope.selectPayment=(type)=>{
        $scope.order.paymentType = type;
    }

    //提交订单
    $scope.submitOrder=()=>{
        alert("正在提交订单");
        $scope.order.receiverAreaName= $scope.addr.address;
        $scope.order.receiver = $scope.addr.contact;
        $scope.order.receiverMobile = $scope.addr.mobile;
        cartService.submitOrder($scope.order).success(response=>{
            if (response.success){
                location.href = "paysuccess.html";
            } else {
                location.href = "payfail.html";
            }
        })
    }

})