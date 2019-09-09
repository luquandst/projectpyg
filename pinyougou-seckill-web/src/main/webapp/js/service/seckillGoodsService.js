app.service("seckillGoodsService",function ($http) {
    //查询所有的商品
    this.findAll=()=>{
        return $http.get("./seck/findAll.do");
    }

    //查询单个秒杀商品的详情
    this.findOne=(id)=>{
        return $http.get("./seck/findOne.do?id="+id);
    }
})