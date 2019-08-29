 //控制层 
app.controller('goodsController' ,function($scope,$controller,typeTemplateService,$location ,itemCatService,uploadService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承

	//编辑页面一加载的时候，就根据管理页面传过来的商品id，查询当前商品信息并展示在编辑页面上
	$scope.findOne=()=>{
		//拿到传过来的商品id
        var id = $location.search()["id"];
		//查询当前的商品的信息
		// alert("正在跳转到新的页面....");
		goodsService.findOne(id).success(responese=>{
			$scope.entity = responese;
			//给富文本编辑器的内容进行赋值
            editor.html($scope.entity.goodsDesc.introduction);
            //将商品图片转换成json对象
			$scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
			//给扩展属性转换格式
			$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
			//转换规格列表
            $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
            //将规格列表中spec转换
			var items = $scope.entity.items;
			for (var i=0;i<items.length;i++){
				items[i].spec = JSON.parse(items[i].spec);
			}
		})
	}

	//根据查询到的规格选项是选择框处于选中状态
	$scope.checkAttribute=(spec,option)=>{
		//判断规格选项的列表中是否含有该规格选项，有的话，就返回该对象，没有的话，就返回空
		var items = $scope.entity.goodsDesc.specificationItems;
		var obj = searchObjectByKey(items,"attributeName",spec);
		//判断obj是否为空
		if (obj == null){
			return false;
		} else {
			//判断spec中是否含有option，含有的话，就返回true
			return obj.attributeValue.indexOf(option) >= 0;
		}
	}


	//定义一个函数，当点击修改的时候，页面跳转到编辑的页面
	$scope.toUpdate=(id)=>{
		alert(id);
        location.href="goods_edit.html#?id="+id;
	}

	//定义一个审核状态的数组
	$scope.status=["未审核","已审核","审核未通过","已关闭"];
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


	//页面加载的时候就加载一级菜单
	$scope.findByParentId=(parentId)=>{
		itemCatService.findByParentId(parentId).success(respone=>{
			$scope.itemCatList1 = respone;
		})
	}

	//当一级菜单的id发生改变的时候，对应的二级菜单也会发生改变
	$scope.$watch("entity.goods.category1Id",function (newValue,oldValue) {
		//根据一级菜单查询所有的二级菜单
		itemCatService.findByParentId(newValue).success(response=>{
            $scope.itemCatList2 = response;
		})
    })

	//当二级菜单的id发生改变的话，对应的三级菜单也会发生改变
	$scope.$watch("entity.goods.category2Id",function (newValue,oldValue) {
		//根据二级菜单查询出所有的三级菜单
		itemCatService.findByParentId(newValue).success(response=>{
			$scope.itemCatList3 = response;
		})
		//根据模板id，查询出所有的规格列表
        typeTemplateService.findSpecByTypeId(newValue).success(response=>{
			$scope.specList1 = response;
		})
    })

	//当三级菜单发生改变的时候，对应的模板id也发生改变
	$scope.$watch("entity.goods.category3Id",function (newValue,oldValue) {
		// alert(newValue);
		//根据三级菜单查询出相应的模板id
		itemCatService.findOne(newValue).success(response=>{
			// alert(response);
            $scope.entity.goods.typeTemplateId = response.typeId;

		})
    })

	//根据模板id查询当前模板下的所有的品牌信息
	$scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {
		//根据模板id查询当前brandIds
		typeTemplateService.findOne(newValue).success(response=>{
			//查询此模板id对应的所有的品牌
			$scope.brandList = JSON.parse(response.brandIds);
			//查询此模板id对应的所有的扩展属性,从页面中获取id，只有当id为空的时候，才算是增加
			var id = $location.search()["id"];
			if (id == null) {
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
			}
		})
		//根据模板id查询所对应的规格
		typeTemplateService.findSpecByTypeId(newValue).success(response=>{
			$scope.specList = response;
		})

    })

    /**
	 * 定义一个方法，判断数组中是否存在某个元素，存在的话，就返回该元素,不存在的话，就返回空
	 * 定义此方法用于判断点击选项的时候，判断是否存在此元素
     */
    searchObjectByKey=(list,key,value)=>{
        for(var i = 0;i < list.length;i++){
            if(list[i][key] == value){
                return list[i];
            }
        }
        return null;
    }

	//点击规格选项的时候，更新下面出现的列表
	$scope.updateSelectOption=(event,name,value)=>{
    	// alert("正在动态生成下属列表...");
        // alert(value);
        // alert(name);
    	//在$scope.entity.goodsDesc.specificationItems查询指定的对象
		var obj = searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name)
		//判断对象是否存在
		if (obj){
			// alert("当前对象存在....");
			//判断规格选项的选中状态
			if (event.target.checked){
				// alert("处于选中状态....");
				//选中的话就往规格选项的属性中添加这个值
				obj.attributeValue.push(value);
			}else {
				//如果没有选中的话，就从数组中移除该值
				var index = obj.attributeValue.indexOf(value);
				obj.attributeValue.splice(index,1);
				//如果某个attributeValue为空的话，就从attributeValue中删除这个元素
				if (obj.attributeValue.length == 0){
					//得到当前obj在scope.entity.goodsDesc.specificationItems中的索引
					var index1 = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
					//删除当前obj
                   $scope.entity.goodsDesc.specificationItems.splice(index1,1);
				}
			}
		}else {
            alert("当前对象不。。存在....");
			//如果该对象不存在的话，就往entity.goodsDesc.specificationItems中添加对象
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
			alert($scope.entity.goodsDesc.specificationItems);
		}
		//生成底部的规格选项的表格
		creatItemList();
	}

	//生成sku商品列表
	creatItemList=()=>{
    	//给sku商品表设置初始值
		$scope.entity.items = [{spec:{},num:0,price:999,isDefault:0,status:0}];
		//获取规格列表
		var items = $scope.entity.goodsDesc.specificationItems;
		//遍历规格列表，动态生成新的商品列表
		for (var i =0;i<items.length;i++){
            $scope.entity.items=addColumn($scope.entity.items,items[i].attributeName,items[i].attributeValue);
		}
	}

	//动态生成sku的列表
	addColumn=(items,attributeName,attributeValue)=>{
    	//定义一个空的数组用于存放动态生成的sku列表
		var newList =[];
		//遍历传入的规格列表,将原来的数据进行深度克隆，这样保证新数据和就数据库之间不会相互影响
		for (var i = 0;i<items.length;i++){
			//得到原来的每行数据
			var oldRow = items[i];
			//将原来的数据进行深度克隆
			for (var j=0;j< attributeValue.length;j++){
				//对原来的行进行深度克隆
				var newRow = JSON.parse(JSON.stringify(oldRow));
				//对新行属性进行赋值
				newRow.spec[attributeName] = attributeValue[j];
				//将新行添加到到数组中去
				newList.push(newRow);
			}

		}
		return newList;

	}




	//文件上传
    $scope.uploadFile=()=>{
        uploadService.uploadFile().success(response=>{
        	// alert("controller正在上传..");
            if(response.success){
                //1。上传成功
                $scope.image_entity.url=response.message;
                alert(response.message);
            }else{
                alert(response.message);
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



    $scope.addItemsImage=()=>{
    	// $scope.image_entity={};
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.removePic=(index)=>{
        $scope.entity.goodsDesc.itemImages.splice( index,1);
    }

    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
	//点击保存的时候就提交
	$scope.save=()=>{
		alert("正在保存..");
		//得到服务层的对象
		var serviceObject;
		//获取富文本编辑器里面的内容
		$scope.entity.goodsDesc.introduction = editor.html();
		//判断商品的id 是否存在，存在的话就是修改，不存在的话就是添加
		if ($scope.entity.goods.id){
			//保存
			serviceObject = goodsService.update($scope.entity);
		} else {
			// alert("正在添加....")
			serviceObject = goodsService.add($scope.entity);
		}
		//判断保存的结果
		serviceObject.success(response=>{
			if (response.success){
				alert("保存成功!")
				//清空编辑页面的内容
				$scope.entity = {};
				//清空富文本编辑器中的内容
                editor.html("");
			}else {
				alert("保存失败");
			}
		})
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

	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
