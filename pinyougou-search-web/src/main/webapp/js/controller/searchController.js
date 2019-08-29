app.controller("searchController",function ($scope,$controller ,searchService) {

    //继承baseController
    $controller("baseController",{$scope:$scope});

    $scope.search=()=>{
        searchService.search($scope.searchMap).success(response=>{
            alert("正在搜索");
            $scope.resultMap = response;
        })
    }
})