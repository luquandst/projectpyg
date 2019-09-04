app.controller("contentController",function ($location,$scope,$controller,contentService) {
    $controller('baseController',{$scope:$scope});//继承

    //定义数组存放广告列表
    $scope.contentList = [];
    //根据分类id查询广告列表
    $scope.findContentByCategoryId=(categoryId)=>{
        alert("查询所有的分类");
        contentService.findContentByCategoryId(categoryId).success(response=>{
           for(var i = 0,len = response.length;i < len;i++){
               $scope.contentList[categoryId] = response;
           }
        })
    }

    //根据关键字搜索，跳转到指定搜索模块的页面
    $scope.searchByKeywords=()=>{
        alert("正在跳转其他模块的页面"+$scope.keywords);
        location.href="http://localhost:9104#?keywords="+$scope.keywords;
    }

})