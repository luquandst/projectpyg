app.controller("searchController",function ($scope,$controller ,searchService) {

    //继承baseController
    $controller("baseController",{$scope:$scope});
    //添加搜索项,设置搜索的初始值
    // $scope.searchMap={'keywords':'','category':'','brand':'','spec':{}};
    $scope.searchMap = {"keywords":'',"category":'',"brand":'',"spec":{}};

    $scope.search=()=>{
        // alert("正在查询数据...")
        searchService.search($scope.searchMap).success(response=>{
            // alert("正在搜索");
            $scope.resultMap = response;
        })
    }
    //点击分类的时候，就往searchMap中添加或减少值
    $scope.addOptions=(key,value)=>{
        //判断点击的选项
        if (key == 'category' || key == 'brand'){
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }
    
    //点击移除面包屑导航的时候移除面包屑导航
    $scope.removeOption=(key,value)=>{
        if (key == "category" || key=="brand"){
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
})