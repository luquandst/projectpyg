app.controller("searchController", function ($location,$scope, $controller, searchService) {

    //继承baseController
    $controller("baseController", {$scope: $scope});
    //添加搜索项,设置搜索的初始值
    $scope.searchMap = {
        "keywords": '',
        "category": '',
        "brand": '',
        "spec": {},
        "price": '',
        "sort": '',
        "sortField": '',
        "startPage": 1,
        "pageSize": 10
    };

    //获取跳转过来的关键词
    $scope.searchMap.keywords = $location.search()["keywords"];

    $scope.search = () => {
        // alert("正在查询数据...")
        searchService.search($scope.searchMap).success(response => {
            $scope.resultMap = response;
        })
    }

    //搜索的时候判断搜索的关键字是否品牌，含有的话，就不显示下面的品牌
    $scope.isContainBrand =()=>{
        var brandList = $scope.resultMap.brandList;
        var keywords = $scope.searchMap.keywords;
        //判断brandList中是否当前搜索的关键字
        for (var i = 0,len = brandList.length;i<len;i++){
            if (keywords.indexOf(brandList[i].text) >= 0){
                // alert("含有品牌");
                return false;
            }
        }
        return true;
    }

    //定义一个集合，用于存放当前的页面
    $scope.pageList = [1, 2, 3];
    //定义两个变量，用做判断的分页标签前后..存在的一句
    // $scope.firstDot = true;
    // $scope.lastDot = true;
    //用户点击当前页的时候，会根据当前的页码查询出页面内容
    $scope.listPage = (page) => {
        alert("正在分页查询!!!" + page);
        //判断总的页数的值
        if ($scope.resultMap.totalPages < 3) {
            // alert("页数小于300");
            //遍历总的页数，给页面的集合进行赋值
            for (var i = 0; i < $scope.resultMap.totalPages; i++) {
                $scope.pageList[i] = i;
                // $scope.firstDot = false;
                // $scope.lastDot = false;
            }
        } else {
            //如果当前页的值小于2的话，就展示当前所有角标
            if (page == 1) {
                alert("当前为第一页");
                $scope.pageList = [1, 2, 3];
                // $scope.firstDot = false;
            } else
            //如果当前页是最后一页的话，就展示最后三页
            if (page == $scope.resultMap.totalPages) {
                alert("当前为最后一页");
                $scope.pageList = [parseInt($scope.resultMap.totalPages) - 2, parseInt($scope.resultMap.totalPages) - 1, parseInt($scope.resultMap.totalPages)];
                // $scope.lastDot = false;
            } else if (page > 1 && page < $scope.resultMap.totalPages) {
                //如果都不是的话，集合中就存放当前页，上一页和下一页
                $scope.pageList = [parseInt(page) - 1, parseInt(page), parseInt(page) + 1];
          /*      if (page > 2){
                    // $scope.firstDot = true;
                }
                if (page < parseInt($scope.resultMap.totalPages) - 2) {
                    // $scope.lastDot = true;
                }*/
            } else {
                alert("页面不存在");
            }


        }
        //给startPage进行赋值
        $scope.searchMap.startPage = parseInt(page);
        //调用search方法查询内容
        $scope.search();
    }


    //定义一个方法，用于排序的查询
    $scope.sortItem = (key, value) => {
        //给排序的字段以及升序或降序进行赋值
        $scope.searchMap.sort = key;
        $scope.searchMap.sortField = value;
        //重新进行查询
        $scope.search();
    }

    //点击分类的时候，就往searchMap中添加或减少值
    $scope.addOptions = (key, value) => {
        //
        //判断点击的选项
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }

    //点击移除面包屑导航的时候移除面包屑导航
    $scope.removeOption = (key, value) => {
        if (key == "category" || key == "brand" || key == 'price') {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
})