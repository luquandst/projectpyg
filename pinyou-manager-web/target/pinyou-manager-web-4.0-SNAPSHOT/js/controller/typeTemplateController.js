 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller,brandService,typeTemplateService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承


	//查询所有的商品
    $scope.findBrandList=()=>{
    	// alert("加载所有的品牌");
    	brandService.findAllByList().success(response=>{
    		// alert({data:response});
            // alert(response);
            $scope.brandList={data:response};
		})
	}

	//查询所有的规格
	$scope.findSpecList =()=>{
    	specificationService.findAllByList().success(response=>{
    		// alert(response);
            $scope.specList={data:response};
		})
	}

    //添加扩展属性
    $scope.addCustomAttr=()=>{
        $scope.entity.customAttributeItems.push({});
    }

    //删除扩展属性
	$scope.removeCustomAttr=(index)=>{
        $scope.entity.customAttributeItems.splice(index,1);
	}


    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}

	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//点击修改的时候要转化对象的类型
    $scope.updateUI=(template)=> {
        $scope.entity = template;
        //将json串转换为json对象
        $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
        $scope.entity.specIds = JSON.parse($scope.entity.specIds);
        $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
    }
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		// alert($scope.entity.id);
		if($scope.entity.id){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//定义一个方法,将后端传回来的json数据转化成对象
	$scope.getInfo=(list,text)=>{
		//定义一个字符串
		var info = "";
		//json转化为对象
        list = JSON.parse(list);
        for (var i = 0;i<list.length;i++){
        	var obj = list[i];
        	info += obj[text]+"";
		}
		return info.substring(0,info.length);
	}
    
});	
