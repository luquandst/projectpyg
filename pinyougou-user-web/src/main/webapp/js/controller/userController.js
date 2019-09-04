app.controller("userController",function ($scope,$controller,userService) {
    $controller('baseController',{$scope:$scope});//继承

    //获取验证码
    $scope.getValidCode=()=>{
        alert("调用controller层获取验证码")
        userService.getValidCode($scope.entity.phone).success(response=>{
            alert(response.message);
        })
    }

    //用户注册
    $scope.reg=()=>{
        //在注册之前先要判断用户两次输入的密码是否一致
        if ($scope.entity.password != $scope.repassword){
            //提示用户两次输入的密码不一致
            alert("两次输入的密码不一致");
            return;
        }
        //输入密码一致就调用service层的方法进行注册
        userService.reg($scope.entity,$scope.validCode).success(response=>{
            //清空内容
            $scope.entity ={};
            alert(response.message);
        })
    }

})