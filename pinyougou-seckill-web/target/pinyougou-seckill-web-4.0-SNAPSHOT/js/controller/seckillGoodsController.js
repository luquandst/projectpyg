app.controller("seckillGoodsController",function ($scope,$controller,seckillGoodsService,$location,$interval) {
    $controller('baseController',{$scope:$scope});//继承

    //查询所有的秒杀商品
    $scope.findAll=()=>{
        // alert("正在查询目前所有的秒杀订单");
        seckillGoodsService.findAll().success(response=>{
            $scope.list=response;
        })
    }

    //跳转到商品详情的界面
    $scope.skip=(id)=>{
        location.href="seckill-item.html#?id="+id;
    }

    //根据秒杀商品的id查询当前的秒杀商品的详情
    $scope.findOne=()=>{
        //获取传过来的商品id
        var id = $location.search()["id"];
        // alert("当前商品的id为"+id);
        //查询商品的详情
        seckillGoodsService.findOne(id).success(response=>{
            $scope.entity = response;
            //显示当前活动结束时间
            showTime();
        })
    }

    //动态显示距离当前活动结束的时间
    showTime=()=>{
        $scope.info="";
        var flag = $interval(()=>{
            //获取活动结束距离现在的时间差
            var time = Math.floor((new Date($scope.entity.endTime).getTime()- new Date().getTime())/1000);
            //获取当前剩余的天数
            var day = Math.floor(time/(3600*24));
            //获取小时
            var hour = Math.floor((time - day*3600*24)/3600);
            //获取剩余的分钟
            var minute = Math.floor((time-day*3600*24-hour*3600)/60);
            //获取剩余的秒
            var second = time - day*3600*24 - hour*3600 - minute*60;
            $scope.info = day +"天"+hour+":"+minute+":"+second;
            //如果时间等于0，秒杀结束
            if (time <= 0){
                $interval.cancel(flag);
            }
        })
    }

})