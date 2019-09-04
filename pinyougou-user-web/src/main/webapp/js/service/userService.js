app.service("userService",function ($http) {
    //通过手机号请求后台发送验证码
    this.getValidCode=(phone)=>{
        alert("调用service层生成验证码");
        return $http.get("./user/getValidCode.do?phone="+phone);
    }
    //到后台进行注册
    this.reg=(entity,validCode)=>{
        return $http.post("./user/reg.do?validCode="+validCode,entity);
    }
})