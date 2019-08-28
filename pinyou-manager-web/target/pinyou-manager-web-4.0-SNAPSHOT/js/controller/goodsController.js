 //控制层 
app.controller('goodsController' ,function($scope,$controller  ,itemCatService ,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承

	//审核商品
	$scope.updateStatus=(status)=>{
		goodsService.updateStatus($scope.selectIds,status).success(response=>{
			if (response.success){
				//重新加载页面
                $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);//重新加载
				alert(response.message);
			}else {
				alert(response.message);
			}
		})
	}

    //定义状态值数组
    $scope.status=["未审核","己审核","审核未通过","己关闭"];
    //定义一个全局的分类的数组
    $scope.categoryList = [];
    //查询所有商品的分类的信息
    $scope.findCategory=()=>{
        itemCatService.findAll().success(response=>{
            //遍历查询结果，将分类的id作为数组的key，分类的名称作为分组的值存放到分类数组中去
            for (var i = 0;i<response.length;i++){
                $scope.categoryList[response[i].id] = response[i].name;
            }
        })
    }
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.goodsList=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
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
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
                    $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);//重新加载
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
