 //控制层 
app.controller('loginController' ,function($scope,loginService ){

	//获取当前登录用户的用户名
    $scope.getUser=()=>{
    	// alert("查找用户名");
    	loginService.getUser().success(response=>{
    		// alert(response.name);
    		$scope.name = response.name;
		})
    }
    
});	
