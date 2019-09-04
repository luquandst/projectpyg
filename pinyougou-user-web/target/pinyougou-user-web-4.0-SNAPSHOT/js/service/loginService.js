app.service("loginService",function ($http) {
    //获取当前的登录的用户
    this.getUserName=()=>{
        alert("正在加载service");
        return $http.get("./getUserName.do");
    }
})