 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承

	//根据父id查询当前节点的列表
	$scope.findByParentId=(parentId)=>{
		itemCatService.findByParentId(parentId).success(response=>{
			$scope.list=response;
		})
	}

	$scope.pid = 0;
	//点击查看下一级的时候就查询当前节点下的所有子节点
	$scope.selectList=(p_entity)=>{
		//判断当前的等级
		if ($scope.grade == 1){
			//把当前的entity设置的为父类
			$scope.entity = p_entity;
			$scope.pid
			//设置其他的entity为空
			$scope.entity_1 = null;
			$scope.entity_2 = null;
		} else if ($scope.grade == 2){
			//当前的列表等级为二级
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
		} else if ($scope == 3){
			//当前的列表等级为三级
			$scope.entity_2 = p_entity;
		}
		//根据当前节点的id查询出所有下属节点的列表
		$scope.findByParentId(p_entity.id);
	}

	//定义一个全局的变量来判断当前等级
	$scope.grade = 1;
	//每次点击的时候就改变当前节点的等级
	$scope.setGrade=(grade)=>{
		$scope.grade =grade;
	}


	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
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
		itemCatService.dele( $scope.selectIds ).success(
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
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
