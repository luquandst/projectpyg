//服务层
app.service('uploadService',function($http){

/*	//上传图片
	this.uploadFile=()=>{
	    alert("service也在上传...");
		//构造文件上传的数据
        var formData = new FormData();
        formData.append("file",file.files[0]);
        //上传图片
        return $http({
            method:"post",  //上传的方法
            datalert("service也在上传...");a:formData,   //文件
            url:"../upload.do", //请求地址
            headers:{"Content-Type":undefined}, //文件头
            transformRequest:angular.identity //序列化
		})*/

    //1。文件上传
    this.uploadFile=()=>{
        alert("service也在上传...");
        //1.1)构造文件上传的数据
        var formData = new FormData();
        formData.append("file",file.files[0]);
        //1.2)向后台发出上传请求
        return $http({
            method:"post",
            data:formData,
            url:"../upload.do",
            headers:{"Content-Type":undefined},
            transformRequest:angular.identity
        })
    }

});
