//服务层
app.service('loginService',function($http){

	//获取当前登录的用户用户名
	this.getUser=()=>{
		return $http.get("../login.do");
	}
});
