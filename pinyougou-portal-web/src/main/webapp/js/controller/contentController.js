app.controller("contentController",function ($scope,$controller,contentService) {
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

})