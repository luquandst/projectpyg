app.controller("indexController",function ($scope,$controller,loginService) {
    $controller('baseController',{$scope:$scope});//继承

    //获取当前登录的用户名
    $scope.loginName=()=>{
        alert("正在加载用户名");
        loginService.getUserName().success(response=>{
            alert(response);
            $scope.name = response.name;
        })
    }

})